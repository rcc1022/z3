package org.lpw.photon.ctrl.upload;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.storage.Storage;
import org.lpw.photon.storage.Storages;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Image;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Validator;
import org.lpw.photon.wormhole.WormholeHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service(UploadService.PREFIX + "service")
public class UploadServiceImpl implements UploadService, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Json json;
    @Inject
    private Message message;
    @Inject
    private DateTime dateTime;
    @Inject
    private Generator generator;
    @Inject
    private Image image;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private Storages storages;
    @Inject
    private WormholeHelper wormholeHelper;
    @Inject
    private JsonConfigs jsonConfigs;
    @Inject
    private Optional<Set<UploadInterceptor>> interceptors;
    @Value("${photon.ctrl.upload-fail:999994}")
    private int failureCode;
    private Map<String, UploadListener> listeners;

    @Override
    public JSONArray uploads(String content) {
        if (validator.isEmpty(content))
            return new JSONArray();

        JSONArray uploads = json.toArray(content);
        if (validator.isEmpty(uploads))
            return new JSONArray();

        List<UploadReader> uploadReaders = new ArrayList<>();
        for (int i = 0, size = uploads.size(); i < size; i++)
            uploadReaders.add(new SimpleUploadReader(json.toMap(uploads.getJSONObject(i))));

        try {
            return uploads(uploadReaders);
        } catch (IOException e) {
            logger.warn(e, "处理JSON方式上传文件时发生异常！");

            return new JSONArray();
        }
    }

    @Override
    public JSONArray uploads(List<UploadReader> uploadReaders) throws IOException {
        JSONArray array = new JSONArray();
        for (UploadReader uploadReader : uploadReaders)
            array.add(upload(uploadReader));

        return array;
    }

    @Override
    public JSONObject upload(Map<String, String> map) {
        try {
            return upload(new SimpleUploadReader(map));
        } catch (IOException e) {
            logger.warn(e, "处理文件[{}:{}:{}]上传时发生异常！", map.get("name"), map.get("fileName"), map.get("contentType"));

            return new JSONObject();
        }
    }

    @Override
    public JSONObject upload(UploadReader uploadReader) throws IOException {
        String name = uploadReader.getName();
        UploadListener uploadListener = getListener(name);
        if (uploadListener == null)
            return failure(uploadReader, message.get(PREFIX + "listener.not-exists", name));

        String contentType = uploadListener.getContentType(uploadReader);
        if (!uploadListener.isUploadEnable(uploadReader)) {
            logger.warn(null, "无法处理文件上传请求[key={}&content-type={}&file-name={}]！", name, contentType,
                    uploadReader.getFileName());

            return failure(uploadReader, message.get(PREFIX + "disable", name, contentType, uploadReader.getFileName()));
        }

        Set<UploadInterceptor> interceptors = this.interceptors.orElse(null);
        if (!validator.isEmpty(interceptors))
            for (UploadInterceptor interceptor : interceptors)
                if (!interceptor.enable(uploadReader, uploadListener, contentType))
                    return failure(uploadReader, message.get(PREFIX + "disable", name, contentType, uploadReader.getFileName()));

        JSONObject object = uploadListener.settle(uploadReader);
        if (object == null)
            object = save(uploadListener, uploadReader, contentType);
        uploadReader.delete();
        uploadListener.complete(uploadReader, object);
        String message = uploadListener.message();

        if (!validator.isEmpty(interceptors))
            for (UploadInterceptor interceptor : interceptors)
                interceptor.complete(uploadReader, uploadListener, contentType, object);

        return validator.isEmpty(message) ? object : pack(0, object, message);
    }

    private UploadListener getListener(String key) {
        if (listeners.containsKey(key))
            return listeners.get(key);

        for (String k : listeners.keySet())
            if (validator.isMatchRegex(k, key))
                return listeners.get(k);

        UploadListener uploadListener = jsonConfigs.get(key);
        if (uploadListener == null)
            logger.warn(null, "无法获得上传监听器[{}]，文件上传失败！", key);

        return uploadListener;
    }

    private JSONObject save(UploadListener uploadListener, UploadReader uploadReader, String contentType) throws IOException {
        Storage storage = storages.get(uploadListener.getStorage());
        if (storage == null) {
            logger.warn(null, "无法获得存储处理器[{}]，文件上传失败！", uploadListener.getStorage());

            return failure(uploadReader, message.get(PREFIX + "storage.not-exists", uploadListener.getStorage()));
        }

        JSONObject object = new JSONObject();
        object.put("success", true);
        object.put("name", uploadReader.getName());
        object.put("fileName", uploadReader.getFileName());
        object.put("fileSize", uploadReader.getSize());
        String suffix = getSuffix(uploadListener, uploadReader);

        if (storage.getType().equals(Storages.TYPE_DISK)) {
            String path = uploadListener.getPath(uploadReader);
            String whPath = image.is(contentType, uploadReader.getFileName())
                    ? wormholeHelper.image(path, null, suffix, null, uploadReader.getInputStream())
                    : wormholeHelper.file(path, null, suffix, null, uploadReader.getInputStream());
            if (whPath != null) {
                object.put("path", whPath);
                if (logger.isDebugEnable())
                    logger.debug("保存上传文件[{}]。", object);

                return object;
            }
        }

        String path = newSavePath(contentType, uploadListener.getPath(uploadReader), suffix);
        object.put("path", path);
        uploadReader.write(storage, path);
        if (image.is(contentType, path)) {
            String thumbnail = image(object, uploadListener.getImageSize(), storage, contentType, path);
            if (thumbnail != null)
                object.put("thumbnail", thumbnail);
        }
        if (logger.isDebugEnable())
            logger.debug("保存上传文件[{}]。", object);

        return object;
    }

    private String getSuffix(UploadListener uploadListener, UploadReader uploadReader) {
        String suffix = uploadListener.getSuffix(uploadReader);
        if (!validator.isEmpty(suffix))
            return suffix.toLowerCase();

        int indexOf = uploadReader.getFileName().lastIndexOf('.');

        return indexOf == -1 ? "" : uploadReader.getFileName().substring(indexOf).toLowerCase();
    }

    private String image(JSONObject object, int[] size, Storage storage, String contentType, String path) {
        try (InputStream inputStream = storage.getInputStream(path)) {
            BufferedImage image = this.image.read(inputStream);
            if (image == null)
                return null;

            object.put("width", image.getWidth());
            object.put("height", image.getHeight());

            if (size == null || size.length == 0)
                return null;

            image = size.length == 1 ? this.image.scale(image, 1.0D * size[0] / 100)
                    : this.image.thumbnail(image, size[0], size[1]);
            if (image == null)
                return null;

            int indexOf = path.lastIndexOf('.');
            String thumbnail = path.substring(0, indexOf) + ".thumbnail" + path.substring(indexOf);
            this.image.write(image, this.image.formatFromContentType(contentType), storage.getOutputStream(thumbnail));

            return thumbnail;
        } catch (Exception e) {
            logger.warn(e, "生成压缩图片时发生异常！");

            return null;
        }
    }

    private JSONObject failure(UploadReader uploadReader, String message) {
        JSONObject object = new JSONObject();
        object.put("success", false);
        object.put("name", uploadReader.getName());
        object.put("fileName", uploadReader.getFileName());

        return pack(failureCode, object, message);
    }

    private JSONObject pack(int code, JSONObject data, String message) {
        JSONObject object = new JSONObject();
        object.put("code", code);
        object.put("data", data);
        object.put("message", message);

        return object;
    }

    @Override
    public void remove(String key, String uri) {
        UploadListener listener = getListener(key);
        if (listener == null) {
            logger.warn(null, "无法获得上传监听key[{}]，删除失败！", key);

            return;
        }

        storages.get(listener.getStorage()).delete(uri);

        if (logger.isDebugEnable())
            logger.debug("删除上传的文件[{}:{}]。", listener.getStorage(), uri);
    }

    @Override
    public String newSavePath(String contentType, String name, String suffix) {
        String dir = (ROOT + contentType + "/" + name + "/" + dateTime.toString(dateTime.today(), "yyyyMMdd") + "/").replaceAll("/{2,}", "/");
        io.mkdirs(context.getAbsolutePath(dir));

        return dir + generator.random(32) + suffix;
    }

    @Override
    public String getContentType(String uri) {
        int indexOf = uri.lastIndexOf(ROOT);
        if (indexOf == -1)
            return null;

        String[] ct = uri.substring(indexOf + ROOT.length()).split("/");

        return ct.length > 1 ? (ct[0] + "/" + ct[1]) : null;
    }

    @Override
    public int getContextRefreshedSort() {
        return 19;
    }

    @Override
    public void onContextRefreshed() {
        if (listeners != null)
            return;

        listeners = new HashMap<>();
        BeanFactory.getBeans(UploadListener.class).forEach(listener -> listeners.put(listener.getKey(), listener));
    }
}
