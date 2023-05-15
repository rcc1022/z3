package org.lpw.photon.dao.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@Repository("photon.model.helper")
public class ModelHelperImpl implements ModelHelper {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ModelTables modelTables;

    @Override
    public <T extends Model> Object get(T model, String name) {
        return modelTables.get(getModelClass(model.getClass())).get(model, name);
    }

    @Override
    public <T extends Model> void set(T model, String name, Object value) {
        modelTables.get(getModelClass(model.getClass())).set(model, name, value);
    }

    @Override
    public <T extends Model> JSONObject toJson(T model) {
        return toJson(model, new HashSet<>());
    }

    @Override
    public <T extends Model> JSONObject toJson(T model, Set<String> ignores) {
        if (model == null)
            return null;

        if (ignores == null)
            ignores = new HashSet<>();

        JSONObject object = new JSONObject();
        ModelTable modelTable = modelTables.get(getModelClass(model.getClass()));
        if (!ignores.contains("id"))
            object.put("id", model.getId());
        for (String name : modelTable.getPropertyNames()) {
            if (ignores.contains(name))
                continue;

            Jsonable jsonable = modelTable.getJsonable(name);
            if (jsonable == null)
                continue;

            Object value = modelTable.get(model, name);
            if (jsonable.extend()) {
                JSONObject json = this.json.toObject(value);
                if (json == null)
                    continue;

                JSONObject extend = object.containsKey("extend") ? object.getJSONObject("extend") : new JSONObject();
                json.forEach((k, v) -> {
                    if ("id".equals(k) || modelTable.containsPropertyName(k))
                        extend.put(k, v);
                    else
                        object.put(k, v);
                });
                if (!extend.isEmpty())
                    object.put("extend", extend);

                continue;
            }

            Object json = getJson(modelTable, name, value, jsonable);
            if (json != null) {
                object.put(name, json);
                if (jsonable.timestamp() && value instanceof Timestamp timestamp)
                    object.put(name + "Timestamp", timestamp.getTime());
            }
        }

        return object;
    }

    private <T extends Model> Object getJson(ModelTable modelTable, String name, Object value, Jsonable jsonable) {
        if (value == null)
            return null;

        if (value instanceof Number)
            return value;

        if (value instanceof Model model)
            return toJson(model);

        if (value instanceof Collection collection) {
            JSONArray array = new JSONArray();
            if (!validator.isEmpty(collection))
                for (Object object : collection)
                    array.add(getJson(modelTable, name, object, jsonable));

            return array;
        }

        if (value instanceof Date date) {
            Class<?> type = modelTable.getType(name);
            if (type == null)
                return null;

            if (Timestamp.class.equals(type))
                return converter.toString(new Timestamp(date.getTime()));

            return converter.toString(new java.sql.Date(date.getTime()));
        }

        String format = jsonable.format();
        if (!validator.isEmpty(format)) {
            if (format.startsWith("number.")) {
                int[] ns = numeric.toInts(format.substring(7));
                return converter.toString(value, ns[0], ns[1]);
            }
        }

        return converter.toString(value);
    }

    @SuppressWarnings("unchecked")
    private <T extends Model> Class<T> getModelClass(Class<T> modelClass) {
        if (modelClass.getName().endsWith("Model"))
            return modelClass;

        return getModelClass((Class<T>) modelClass.getSuperclass());
    }

    @Override
    public <T extends Model> JSONArray toJson(Collection<T> models) {
        return toJson(models, new HashSet<>());
    }

    @Override
    public <T extends Model> JSONArray toJson(Collection<T> models, BiConsumer<T, JSONObject> biConsumer) {
        return toJson(models, new HashSet<>(), biConsumer);
    }

    @Override
    public <T extends Model> JSONArray toJson(Collection<T> models, Set<String> ignores) {
        return toJson(models, ignores, null);
    }

    @Override
    public <T extends Model> JSONArray toJson(Collection<T> models, Set<String> ignores, BiConsumer<T, JSONObject> biConsumer) {
        return toJson(models, model -> {
            JSONObject object = toJson(model, ignores);
            if (biConsumer != null)
                biConsumer.accept(model, object);

            return object;
        });
    }

    @Override
    public <T extends Model> JSONArray toJson(Collection<T> models, Function<T, JSONObject> function) {
        JSONArray array = new JSONArray();
        if (validator.isEmpty(models))
            return array;

        models.forEach(model -> array.add(function.apply(model)));

        return array;
    }

    @Override
    public <T extends Model> T fromJson(JSONObject json, Class<T> modelClass) {
        if (json == null || modelClass == null)
            return null;

        return fromJson(json, modelTables.get(modelClass), modelClass);
    }

    @Override
    public <T extends Model> List<T> fromJson(JSONArray array, Class<T> modelClass) {
        if (array == null || modelClass == null)
            return null;

        List<T> list = new ArrayList<>();
        ModelTable modelTable = modelTables.get(modelClass);
        for (int i = 0, size = array.size(); i < size; i++)
            list.add(fromJson(array.getJSONObject(i), modelTable, modelClass));

        return list;
    }

    private <T extends Model> T fromJson(JSONObject json, ModelTable modelTable, Class<T> modelClass) {
        T model = BeanFactory.getBean(modelClass);
        if (json.containsKey("id"))
            model.setId(json.getString("id"));

        return toModel(modelTable, model, json);
    }

    @Override
    public <T extends Model> T fromMap(Map<String, String> map, Class<T> modelClass) {
        if (map == null || modelClass == null)
            return null;

        ModelTable modelTable = modelTables.get(modelClass);
        if (modelTable == null)
            return null;

        T model = BeanFactory.getBean(modelClass);
        if (map.isEmpty())
            return model;

        if (map.containsKey("id"))
            model.setId(map.get("id"));

        return toModel(modelTable, model, map);
    }

    private <T extends Model> T toModel(ModelTable modelTable, T model, Map<String, ?> map) {
        JSONObject extend = new JSONObject();
        map.forEach((key, value) -> {
            if (key.equals("id"))
                return;

            if (modelTable.containsPropertyName(key))
                modelTable.set(model, key, value);
            else
                extend.put(key, value);
        });
        if (!extend.isEmpty())
            modelTable.setExtend(model, extend);

        return model;
    }

    @Override
    public <T extends Model> JSONObject getExtend(Class<T> modelClass, Map<String, String> map) {
        JSONObject object = new JSONObject();
        if (validator.isEmpty(map))
            return object;

        ModelTable modelTable = modelTables.get(modelClass);
        map.keySet().stream().filter(key -> !modelTable.containsPropertyName(key)).forEach(key -> object.put(key, map.get(key)));

        return object;
    }

    @Override
    public <T extends Model> void copy(T source, T target, boolean containId) {
        if (source == null || target == null) {
            logger.warn(null, "复制Model源[{}]或目标[{}]为null，无法进行复制！", source, target);

            return;
        }

        modelTables.get(source.getClass()).copy(source, target, containId);
    }

    @Override
    public <T extends Model> String toString(T model) {
        if (model == null)
            return null;

        ModelTable modelTable = modelTables.get(getModelClass(model.getClass()));
        if (modelTable == null)
            return null;

        return modelTable.toString(model);
    }
}
