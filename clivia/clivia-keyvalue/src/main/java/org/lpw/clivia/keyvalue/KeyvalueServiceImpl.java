package org.lpw.clivia.keyvalue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service(KeyvalueModel.NAME + ".service")
public class KeyvalueServiceImpl implements KeyvalueService {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Numeric numeric;
    @Inject
    private Cache cache;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private Optional<Set<KeyvalueListener>> listeners;
    @Inject
    private KeyvalueDao keyvalueDao;

    @Override
    public JSONObject query(String key) {
        return validator.isEmpty(key) ? keyvalueDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson()
                : keyvalueDao.query(key, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray list(String key) {
        return modelHelper.toJson(keyvalueDao.query(key, 0, 0).getList());
    }

    @Override
    public JSONObject object(String key) {
        return cache.computeIfAbsent(cacheKey(":object:" + key), k -> {
            JSONObject object = new JSONObject();
            keyvalueDao.query(key, 0, 0).getList()
                    .forEach(keyvalue -> object.put(keyvalue.getKey(), keyvalue.getValue()));

            return object;
        }, false);
    }

    @Override
    public String value(String key) {
        return cache.computeIfAbsent(cacheKey(":value:" + key), k -> {
            KeyvalueModel keyvalue = keyvalueDao.findByKey(key);

            return keyvalue == null ? "" : keyvalue.getValue();
        }, false);
    }

    @Override
    public int valueAsInt(String key, int defaultValue) {
        return numeric.toInt(value(key), defaultValue);
    }

    @Override
    public double valueAsDouble(String key, double defaultValue) {
        return numeric.toDouble(value(key), defaultValue);
    }

    @Override
    public int[] valueAsInts(String key, String separator) {
        String value = value(key);
        if (validator.isEmpty(value))
            return new int[0];

        if (!value.contains(separator))
            return new int[]{numeric.toInt(value)};

        String[] array = value.split(separator);
        int[] ns = new int[array.length];
        for (int i = 0; i < array.length; i++)
            ns[i] = numeric.toInt(array[i]);

        return ns;
    }

    @Override
    public String[] valueAsArray(String key, String separator) {
        String value = value(key);
        if (validator.isEmpty(value))
            return new String[0];

        if (!value.contains(separator))
            return new String[]{value};

        return value.split(separator);
    }

    @Override
    public boolean exists(String key, String value) {
        String v = value(key);

        return v != null && v.equals(value);
    }

    @Override
    public void save(String key, String value) {
        KeyvalueModel keyvalue = keyvalueDao.findByKey(key);
        if (keyvalue == null) {
            keyvalue = new KeyvalueModel();
            keyvalue.setKey(key);
        }
        keyvalue.setValue(value);
        keyvalueDao.save(keyvalue);
        cleanCache();

        Map<String, String> map = Map.of(key, value);
        listeners.ifPresent(set -> set.forEach(listener -> listener.keyvalueModify(map)));
    }

    @Override
    public void save(KeyvalueModel keyvalue) {
        if (validator.isEmpty(keyvalue.getId()) || keyvalueDao.findById(keyvalue.getId()) == null)
            keyvalue.setId(null);
        keyvalueDao.save(keyvalue);
        cleanCache();

        Map<String, String> map = Map.of(keyvalue.getKey(), keyvalue.getValue());
        listeners.ifPresent(set -> set.forEach(listener -> listener.keyvalueModify(map)));
    }

    @Override
    public void saves(JSONArray array) {
        if (validator.isEmpty(array))
            return;

        Map<String, String> map = new HashMap<>();
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            if (!object.containsKey("key") || !object.containsKey("value"))
                continue;

            String key = object.getString("key");
            if (validator.isEmpty(key))
                continue;

            KeyvalueModel keyvalue = keyvalueDao.findByKey(key);
            if (keyvalue == null) {
                keyvalue = new KeyvalueModel();
                keyvalue.setKey(key);
            }
            keyvalue.setValue(object.getString("value"));
            keyvalueDao.save(keyvalue);
            map.put(keyvalue.getKey(), keyvalue.getValue());
        }
        cleanCache();
        listeners.ifPresent(set -> set.forEach(listener -> listener.keyvalueModify(map)));
    }

    @Override
    public void delete(String id) {
        KeyvalueModel keyvalue = keyvalueDao.findById(id);
        if (keyvalue == null)
            return;

        keyvalueDao.delete(id);
        cleanCache();

        Map<String, String> map = Map.of(keyvalue.getKey(), keyvalue.getValue());
        listeners.ifPresent(set -> set.forEach(listener -> listener.keyvalueDelete(map)));
    }

    private String cacheKey(String key) {
        return KeyvalueModel.NAME + key + cache.computeIfAbsent(KeyvalueModel.NAME, k -> generator.random(8), false);
    }

    private void cleanCache() {
        cache.remove(KeyvalueModel.NAME);
    }
}
