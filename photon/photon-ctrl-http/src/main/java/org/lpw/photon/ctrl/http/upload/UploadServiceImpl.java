package org.lpw.photon.ctrl.http.upload;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.atomic.Closables;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.ctrl.http.Cors;
import org.lpw.photon.ctrl.http.IgnoreUri;
import org.lpw.photon.ctrl.http.ServiceHelper;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(UploadService.PREFIX + "helper")
public class UploadServiceImpl implements UploadService, IgnoreUri, ContextRefreshedListener {
    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Message message;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private Closables closables;
    @Inject
    private Cors cors;
    @Inject
    private ServiceHelper serviceHelper;
    @Value("${" + UploadService.PREFIX + "max-size:1m}")
    private String maxSize;
    private Map<String, Uploader> uploaders;
    private long maxFileSize;

    @Override
    public void upload(HttpServletRequest request, HttpServletResponse response, String uploader) {
        if (logger.isDebugEnable())
            logger.debug("开始处理HTTP文件上传。");

        cors.set(request, response);
        if (cors.is(request, response))
            return;

        try {
            OutputStream outputStream = serviceHelper.setContext(request, response, uploader);
            List<UploadReader> readers = new ArrayList<>();
            Map<String, String> map = new HashMap<>();
            request.getParameterMap().forEach((name, value) -> map.put(name, converter.toString(value)));
            JSONArray array = new JSONArray();
            for (Part part : request.getParts()) {
                if (map.containsKey(part.getName()))
                    continue;

                if (validator.isEmpty(part.getSubmittedFileName())) {
                    logger.warn(null, "上传文件名为空！");

                    continue;
                }

                if (part.getSize() > maxFileSize) {
                    logger.warn(null, "上传文件大小[{}]超过最大限制[{}:{}]！", part.getSize(), maxSize, maxFileSize);
                    JSONObject object = new JSONObject();
                    object.put("success", false);
                    object.put("message", message.get("photon.ctrl-http.upload.over-max", part.getSize(), maxSize, maxFileSize));
                    array.add(object);

                    continue;
                }

                readers.add(new HttpUploadReader(part, map));
            }
            if (readers.isEmpty()) {
                write(response, outputStream, json.toBytes(array.size() == 1 ? array.getJSONObject(0) : array));
                logger.warn(null, "上传文件流为空！");

                return;
            }

            write(response, outputStream, uploaders.get(uploader).upload(readers));
        } catch (Throwable e) {
            logger.warn(e, "处理文件上传时发生异常！");
        } finally {
            closables.close();
        }
    }

    private void write(HttpServletResponse response, OutputStream outputStream, byte[] bytes) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding(context.getCharset(null));
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    @Override
    public Set<String> getIgnoreUris() {
        return Set.of(UPLOAD, UPLOAD_PATH);
    }

    @Override
    public int getContextRefreshedSort() {
        return 9;
    }

    @Override
    public void onContextRefreshed() {
        uploaders = new HashMap<>();
        BeanFactory.getBeans(Uploader.class).forEach(uploader -> uploaders.put(uploader.getName(), uploader));
        maxFileSize = converter.toBitSize(maxSize);
    }
}
