package org.lpw.clivia.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.dao.model.Model;
import org.lpw.photon.dao.model.ModelTable;
import org.lpw.photon.dao.model.ModelTables;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

@Service(ApiModel.NAME + ".service")
public class ApiServiceImpl implements ApiService {
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Numeric numeric;
    @Inject
    private Message message;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private Cache cache;
    @Inject
    private ModelTables modelTables;
    private final Set<String> model = Set.of("model", "pagination");

    @Override
    public JSONArray get() {
        return cache.computeIfAbsent(ApiModel.NAME + "." + context.getLocale().toString(), key -> read(), false);
    }

    private JSONArray read() {
        List<JSONObject> list = new ArrayList<>();
        modelTables.getModelClasses().forEach(modelClass -> {
            try (InputStream inputStream = modelClass.getResourceAsStream("api.json");
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                if (inputStream == null)
                    return;

                io.copy(inputStream, outputStream);
                put(list, modelClass, outputStream.toString());
            } catch (Throwable throwable) {
                logger.warn(throwable, "解析API[{}]元数据时发生异常！", modelClass);
            }
        });
        list.sort(Comparator.comparing(o -> (o.getIntValue("sort") + ":" + o.getString("uri"))));
        JSONArray array = new JSONArray();
        array.addAll(list);
        if (logger.isInfoEnable())
            logger.info("API配置集{}", array);

        return array;
    }

    private void put(List<JSONObject> list, Class<? extends Model> modelClass, String string) {
        JSONObject object = json.toObject(string);
        if (object == null)
            return;

        ModelTable modelTable = modelTables.get(modelClass);
        String name = modelTable.getName();
        name(object, name);
        ctrl(name, object);
        JSONArray services = object.getJSONArray("services");
        String uri = object.getString("uri");
        boolean model = false;
        for (int i = 0, size = services.size(); i < size; i++) {
            JSONObject service = services.getJSONObject(i);
            if (service.containsKey("uri")) {
                String u = service.getString("uri");
                service.put("uri", u.charAt(0) == '/' ? u : (uri + u));
                message(service, "name", name, u, false);
            } else if (json.has(service, "page", "upload") && service.containsKey("upload")) {
                String upload = service.getString("upload");
                message(service, "name", name, upload, false);
                int index = upload.indexOf('.');
                if (index == -1)
                    service.put("upload", name + "." + upload);
                else if (index == 0)
                    service.put("upload", name + upload);
            } else if (json.has(service, "page", "setting")) {
                message(service, "name", name, "setting", false);
                JSONArray array = new JSONArray();
                JSONArray keys = service.getJSONArray("keys");
                for (int j = 0, s = keys.size(); j < s; j++) {
                    String key = keys.getString(j);
                    JSONObject obj = new JSONObject();
                    obj.put("key", key);
                    message(obj, "description", key, "description", true);
                    array.add(obj);
                }
                service.put("keys", array);
            } else if (json.has(service, "page", "dict"))
                message(service, "name", name, "dict", false);
            description(service, "headers", name);
            description(service, "parameters", name);
            if (response(service, name))
                model = true;
        }
        if (model) {
            if (!object.containsKey("model")) {
                JSONObject m = new JSONObject();
                m.put("id", "");
                modelTable.getPropertyNames().forEach(property -> m.put(property, ""));
                object.put("model", m);
            }
            if (object.containsKey("emodel"))
                object.getJSONObject("model").putAll(object.getJSONObject("emodel"));
            response(object, "model", name);
            format(object, "model");
        }
        list.add(object);
    }

    private void name(JSONObject object, String name) {
        if (object.containsKey("name"))
            message(object, "name", name, "", false);
        if (!object.containsKey("name") || object.getString("name").equals(""))
            object.put("name", message.get(name));
        if (object.getString("name").equals(name))
            message(object, "name", name, "name", false);
    }

    private void ctrl(String name, JSONObject object) {
        Object ctrl = BeanFactory.getBean(name + ".ctrl");
        if (ctrl == null)
            return;

        Execute execute = ctrl.getClass().getAnnotation(Execute.class);
        if (execute == null)
            return;

        if (!object.containsKey("sort"))
            object.put("sort", numeric.toInt(execute.code()));
        if (!object.containsKey("uri"))
            object.put("uri", execute.name());
    }

    private void description(JSONObject object, String name, String prefix) {
        if (!object.containsKey(name))
            return;

        JSONArray array = object.getJSONArray(name);
        for (int j = 0, s = array.size(); j < s; j++) {
            JSONObject obj = array.getJSONObject(j);
            String n = obj.getString("name");
            if ((n.equals("id") || json.has(obj, "type", "id")) && validator.isEmpty(obj.get("description")))
                obj.put("description", message.get(ApiModel.NAME + ".id"));
            else
                message(obj, "description", prefix, n, true);
        }
    }

    private boolean response(JSONObject object, String prefix) {
        boolean model = false;
        if (object.containsKey("response")) {
            int n = response(object, "response", prefix);
            if (n > 1)
                format(object, "response");
            model = n == 1;
        } else
            object.put("response", "\"\"");

        return model;
    }

    private int response(JSONObject object, String name, String prefix) {
        Object value = object.get(name);
        if (model.contains(value.toString()))
            return 1;

        if (value instanceof JSONArray) {
            JSONArray array = (JSONArray) value;
            if (array.size() == 1 && model.contains(array.getString(0)))
                return 1;

            object.put(name, response(array, name, prefix));

            return 2;
        }

        if (value instanceof JSONObject) {
            JSONObject obj = (JSONObject) value;
            obj.keySet().forEach(key -> response(obj, key, prefix));

            return 3;
        }

        if (name.equals("id") && validator.isEmpty(value)) {
            object.put(name, message.get(ApiModel.NAME + ".id"));

            return 0;
        }

        object.put(name, message(prefix, value.toString(), name, "description", true));

        return 0;
    }

    private JSONArray response(JSONArray array, String name, String prefix) {
        JSONArray newArray = new JSONArray();
        array.forEach(object -> {
            if (object instanceof JSONObject) {
                JSONObject obj = (JSONObject) object;
                obj.keySet().forEach(key -> response(obj, key, prefix));
                newArray.add(obj);
            } else
                newArray.add(message(prefix, object.toString(), name, "description", true));
        });

        return newArray;
    }

    private void message(JSONObject object, String name, String prefix, String defaultName, boolean period) {
        object.put(name, message(prefix, object.getString(name), defaultName, name, period));
    }

    private String message(String prefix, String value, String property, String name, boolean period) {
        String message = message(prefix, value, period);
        if (message.equals(""))
            message = message(prefix, "." + property + "." + name, period);
        if (message.equals(""))
            message = message(prefix, property, period);
        if (message.equals(""))
            message = message(ApiModel.NAME, value, period);
        if (message.equals(""))
            message = message(ApiModel.NAME, "." + property + "." + name, period);
        if (message.equals(""))
            message = message(ApiModel.NAME, property, period);

        return message;
    }

    private String message(String prefix, String key, boolean period) {
        if (validator.isEmpty(key))
            return "";

        int index = key.indexOf('.');
        if (index == -1)
            key = prefix + "." + key;
        else if (index == 0)
            key = prefix + key;

        String message = this.message.get(key);
        if (message.equals(key))
            return "";

        return period ? (message + this.message.get(ApiModel.NAME + ".period")) : message;
    }

    private void format(JSONObject object, String name) {
        JSON json = object.getObject(name, JSON.class);
        object.put(name, json.toString().replaceAll("\t", "    "));
    }
}
