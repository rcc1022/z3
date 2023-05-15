package org.lpw.photon.ctrl.upload;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(UploadService.PREFIX + "json-configs")
public class JsonConfigsImpl implements JsonConfigs, MinuteJob {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Numeric numeric;
    @Value("${" + UploadService.PREFIX + "json-configs:/WEB-INF/upload}")
    private String configs;
    private Map<String, JsonConfig> map;

    @Override
    public JsonConfig get(String key) {
        if (map == null)
            init();

        return validator.isEmpty(key) ? null : map.get(key);
    }

    private synchronized void init() {
        if (map != null)
            return;

        map = new ConcurrentHashMap<>();
        executeMinuteJob();
    }

    @Override
    public void executeMinuteJob() {
        if (map == null)
            return;

        File[] files = new File(context.getAbsolutePath(configs)).listFiles();
        if (files == null || files.length == 0)
            return;

        for (File file : files) {
            String key = file.getName().substring(0, file.getName().lastIndexOf('.'));
            JsonConfig config = map.get(key);
            if (config != null && config.getLastModify() == file.lastModified())
                continue;

            config = new JsonConfigImpl();
            JSONObject json = this.json.toObject(io.readAsString(file.getPath()));
            JSONObject path = json.getJSONObject("path");
            for (Object contentType : path.keySet())
                config.addPath(contentType.toString(), path.getString(contentType.toString()));
            JSONArray imageSize = json.getJSONArray("image-size");
            config.setImageSize(numeric.toInt(imageSize.get(0)), numeric.toInt(imageSize.get(1)));
            config.setLastModify(file.lastModified());
            map.put(key, config);
        }
    }
}
