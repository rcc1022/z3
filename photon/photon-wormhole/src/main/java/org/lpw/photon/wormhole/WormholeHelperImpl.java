package org.lpw.photon.wormhole;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.crypto.Sign;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@Service("photon.wormhole.helper")
public class WormholeHelperImpl implements WormholeHelper, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Generator generator;
    @Inject
    private Converter converter;
    @Inject
    private Io io;
    @Inject
    private Sign sign;
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Value("${photon.wormhole.root:}")
    private String root;
    @Value("${photon.wormhole.image:}")
    private String image;
    @Value("${photon.wormhole.file:}")
    private String file;
    @Value("${photon.wormhole.temporary:}")
    private String temporary;
    @Value("${photon.wormhole.hosts:}")
    private String hosts;
    private String[] hostArray;

    @Override
    public boolean isImageUri(String uri) {
        return uri.startsWith("/whimg/");
    }

    @Override
    public boolean isFileUri(String uri) {
        return uri.startsWith("/whfile/");
    }

    @Override
    public String getUrl(Protocol protocol, String uri, boolean internal) {
        if (internal)
            return root + uri;

        if (validator.isEmpty(hostArray))
            return "";

        return protocol.get() + hostArray[generator.random(0, hostArray.length - 1)] + uri;
    }

    @Override
    public String getWsUrl(boolean ssl) {
        return getUrl(ssl ? Protocol.Wss : Protocol.Ws, "/whws", false);
    }

    @Override
    public boolean auth(AuthType type, String token, String ticket) {
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        map.put("ticket", ticket);
        String string = post("/whauth/" + type.getName(), null, map);
        JSONObject object = json.toObject(string);
        if (!json.has(object, "code", "0")) {
            logger.warn(null, "添加Wormhole认证[{}:{}]失败！", map, string);

            return false;
        }

        return true;
    }

    @Override
    public String post(String uri, Map<String, String> requestHeaders, Map<String, String> parameters) {
        return http.post(root + uri, null, parameters);
    }

    @Override
    public String post(String uri, Map<String, String> requestHeaders, String parameters) {
        return http.post(root + uri, null, parameters);
    }

    @Override
    public String image(String path, String name, String suffix, String sign, InputStream inputStream) {
        return save(image, path, name, suffix, sign, inputStream);
    }

    @Override
    public String image(String path, String name, String sign, File file) {
        return save(image, path, name, sign, file);
    }

    @Override
    public String file(String path, String name, String suffix, String sign, InputStream inputStream) {
        return save(this.file, path, name, suffix, sign, inputStream);
    }

    @Override
    public String file(String path, String name, String sign, File file) {
        return save(this.file, path, name, sign, file);
    }

    private String save(String url, String path, String name, String suffix, String sign, InputStream inputStream) {
        if (validator.isEmpty(url))
            return null;

        File file = new File(context.getAbsoluteRoot() + getFilename(name, suffix));
        io.write(file.getAbsolutePath(), inputStream);
        String whUrl = save(url, path, name, sign, file);
        io.delete(file);

        return whUrl;
    }

    private String save(String url, String path, String name, String sign, File file) {
        if (validator.isEmpty(url))
            return null;

        Map<String, String> parameters = new HashMap<>();
        if (!validator.isEmpty(path))
            parameters.put("path", path);
        if (!validator.isEmpty(name))
            parameters.put("name", name);
        if (!validator.isEmpty(sign))
            parameters.put("sign-name", sign);
        this.sign.put(parameters, sign);

        Map<String, File> files = new HashMap<>();
        files.put("file", file);

        return http.upload(url, null, parameters, files);
    }

    private String getFilename(String name, String suffix) {
        int indexOf;
        if (!validator.isEmpty(name) && (indexOf = name.lastIndexOf('.')) > -1)
            suffix = name.substring(indexOf);

        if (!validator.isEmpty(suffix))
            return generator.random(32) + suffix;

        return generator.random(32);
    }

    @Override
    public String temporary(String uri) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("uri", uri);

        return http.post(temporary, null, parameters);
    }

    @Override
    public void download(String uri, String file) {
        try (OutputStream outputStream = new FileOutputStream(file)) {
            http.get(getUrl(Protocol.Http, uri, true), null, null, null, outputStream);
        } catch (Throwable throwable) {
            logger.warn(throwable, "下载Wormhole文件[{}:{}]时发生异常！", uri, file);
        }
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        if (validator.isEmpty(root))
            return;

        if (validator.isEmpty(image))
            image = root + "/whimg/save";
        if (validator.isEmpty(file))
            file = root + "/whfile/save";
        if (validator.isEmpty(temporary))
            temporary = root + "/whtemp/copy";
        hostArray = converter.toArray(hosts, ",");
    }
}
