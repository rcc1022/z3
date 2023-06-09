package org.lpw.photon.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

@Component("photon.util.json")
public class JsonImpl implements Json {
    @Inject
    private Validator validator;
    @Inject
    private Xml xml;
    @Inject
    private Logger logger;

    @Override
    public void add(JSONObject json, String key, Object object) {
        if (validator.isEmpty(key))
            return;

        if (!json.containsKey(key)) {
            json.put(key, object);

            return;
        }

        JSONArray array = asArray(json.get(key));
        array.add(object);
        json.put(key, array);
    }

    @Override
    public void addAsArray(JSONObject json, String key, Object object) {
        if (validator.isEmpty(key))
            return;

        Object obj = json.get(key);
        JSONArray array = obj == null ? new JSONArray() : asArray(obj);
        array.add(object);
        json.put(key, array);
    }

    private JSONArray asArray(Object object) {
        if (object instanceof JSONArray array)
            return array;

        JSONArray array = new JSONArray();
        array.add(object);

        return array;
    }

    @Override
    public String[] getAsStringArray(JSONObject json, String key) {
        if (json == null || validator.isEmpty(key) || !json.containsKey(key))
            return new String[0];

        JSONArray array = json.getJSONArray(key);
        String[] strings = new String[array.size()];
        for (int i = 0; i < strings.length; i++)
            strings[i] = array.getString(i);

        return strings;
    }

    @Override
    public JSONObject fromXml(String xml) {
        return this.xml.toJson(xml);
    }

    @Override
    public JSONObject toObject(Object object) {
        return toObject(object, true);
    }

    @Override
    public JSONObject toObject(Object object, boolean nullable) {
        if (object == null)
            return nullable ? null : new JSONObject();

        if (object instanceof JSONObject obj)
            return obj;

        if (object instanceof Map<?, ?> map) {
            JSONObject obj = new JSONObject();
            map.forEach((key, value) -> obj.put(key.toString(), value));

            return obj;
        }

        try {
            if (object instanceof String string) {
                if (validator.isEmpty(string))
                    return nullable ? null : new JSONObject();

                string = string.trim();
                if (string.charAt(0) != '{') {
                    logger.warn(null, "不是有效的JSON格式数据[{}]！", object);

                    return nullable ? null : new JSONObject();
                }

                return JSON.parseObject(string);
            }

            return JSON.parseObject(object.toString());
        } catch (Throwable throwable) {
            logger.warn(throwable, "转化对象[{}]为JSON对象时发生异常！", object);

            return nullable ? null : new JSONObject();
        }
    }

    @Override
    public JSONArray toArray(Object object) {
        return toArray(object, true);
    }

    @Override
    public JSONArray toArray(Object object, boolean nullable) {
        if (object == null)
            return nullable ? null : new JSONArray();

        if (object instanceof JSONArray array)
            return array;

        try {
            if (object.getClass().isArray()) {
                JSONArray array = new JSONArray();
                for (int i = 0, size = Array.getLength(object); i < size; i++) {
                    Object obj = Array.get(object, i);
                    if (obj instanceof String || obj instanceof Number)
                        array.add(obj);
                    else
                        array.add(toObject(obj));
                }

                return array;
            }

            if (object instanceof String string) {
                if (validator.isEmpty(string))
                    return nullable ? null : new JSONArray();

                string = string.trim();
                if (string.charAt(0) != '[') {
                    logger.warn(null, "不是有效的JSON格式数据[{}]！", object);

                    return nullable ? null : new JSONArray();
                }

                return JSON.parseArray(string);
            }

            return JSON.parseArray(object.toString());
        } catch (Throwable throwable) {
            logger.warn(throwable, "转化对象[{}]为JSON数组时发生异常！", object);

            return nullable ? null : new JSONArray();
        }
    }

    @Override
    public String toString(Object object) {
        return JSON.toJSONString(object, SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public byte[] toBytes(Object object) {
        return JSON.toJSONBytes(object, SerializerFeature.DisableCircularReferenceDetect);
    }

    @Override
    public Map<String, String> toMap(JSONObject object) {
        Map<String, String> map = new HashMap<>();
        if (object != null)
            object.keySet().forEach(key -> map.put(key, object.getString(key)));

        return map;
    }

    @Override
    public JSONObject findObject(JSONObject object, String... keys) {
        if (validator.isEmpty(object) || validator.isEmpty(keys))
            return null;

        for (String key : keys) {
            if (!object.containsKey(key))
                return null;

            object = object.getJSONObject(key);
        }

        return object;
    }

    @Override
    public boolean containsKey(JSONObject object, String key) {
        return object != null && key != null && object.containsKey(key);
    }

    @Override
    public boolean hasTrue(JSONObject object, String key) {
        return containsKey(object, key) && object.getBooleanValue(key);
    }

    @Override
    public boolean has(JSONObject object, String key, String value) {
        if (value == null)
            return false;

        return containsKey(object, key) && object.getString(key).equals(value);
    }

    @Override
    public JSONObject copy(JSONObject object) {
        return object == null ? null : toObject(toString(object));
    }

    @Override
    public JSONArray copy(JSONArray array) {
        return array == null ? null : toArray(toString(array));
    }
}
