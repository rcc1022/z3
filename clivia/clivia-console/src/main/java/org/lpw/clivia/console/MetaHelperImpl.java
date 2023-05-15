package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.crosier.CrosierService;
import org.lpw.clivia.user.crosier.CrosierValid;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelTables;
import org.lpw.photon.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service(ConsoleModel.NAME + ".meta")
public class MetaHelperImpl implements MetaHelper, ContextRefreshedListener, CrosierValid {
    @Inject
    private Cache cache;
    @Inject
    private Context context;
    @Inject
    private Converter converter;
    @Inject
    private Message message;
    @Inject
    private Io io;
    @Inject
    private Validator validator;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ModelTables modelTables;
    @Inject
    private UserService userService;
    @Inject
    private CrosierService crosierService;
    @Value("${" + ConsoleModel.NAME + ".console:/WEB-INF/console/}")
    private String console;
    private final Map<String, MetaDeclare> declares = new HashMap<>();
    private final Map<String, String> map = new ConcurrentHashMap<>();
    private final Map<Integer, Set<String>> cacheKeys = new ConcurrentHashMap<>();
    private final Set<String> kup = Set.of("key", "uri", "props");
    private final String[] actions = {"ops", "toolbar"};

    @Override
    public JSONObject get(String key, boolean all) {
        String cacheKey = ConsoleModel.NAME + ".meta:" + key + ":" + all + ":" + userService.grade() + ":"
                + context.getLocale().toString();
        if (!all)
            cacheKeys.computeIfAbsent(userService.grade(), k -> new HashSet<>()).add(cacheKey);

        return cache.computeIfAbsent(cacheKey, k -> {
            JSONObject meta = json.toObject(map.get(key));
            if (meta == null)
                return new JSONObject();

            String uri = meta.getString("uri");
            String[] prefix = new String[]{meta.getString("key")};
            String[] prefixOp = new String[]{prefix[0], ConsoleModel.NAME + ".op"};
            String[] ln = new String[]{"label", "name"};
            String[] lst = new String[]{"label", "service", "type"};
            for (String mk : meta.keySet()) {
                if (kup.contains(mk))
                    continue;

                JSONObject object = meta.getJSONObject(mk);
                JSONArray props = props(meta, object, "props", uri, mk);
                if (!validator.isEmpty(props))
                    object.put("props", props);
                setLabel(all, false, uri, prefix, props, ln);
                if (object.containsKey("search")) {
                    JSONArray search = props(meta, object, "search", uri, "search");
                    setLabel(true, false, uri, prefix, search, ln);
                    object.put("search", search);
                }
                for (String action : actions)
                    if (object.containsKey(action))
                        setLabel(all, true, uri, prefixOp, object.getJSONArray(action), lst);
            }
            if (meta.containsKey("props"))
                setLabel(all, false, uri, prefix, meta.getJSONArray("props"), ln);

            return meta;
        }, false);
    }

    private void setLabel(boolean all, boolean type, String uri, String[] prefix, JSONArray array, String[] k) {
        if (validator.isEmpty(array))
            return;

        for (int i = array.size() - 1; i >= 0; i--) {
            JSONObject obj = array.getJSONObject(i);
            String service = obj.getString(type && !obj.containsKey("service") ? "type" : "service");
            if (!all && !validator.isEmpty(service)) {
                if (service.charAt(0) != '/')
                    service = uri + service;
                if (!json.hasTrue(obj, "permit") && !crosierService.permit(service,
                        obj.containsKey("parameter") ? json.toMap(obj.getJSONObject("parameter")) : new HashMap<>())) {
                    if (json.has(obj, "type", "switch") || json.has(obj, "type", "refresh"))
                        obj.remove("service");
                    else {
                        array.remove(i);

                        continue;
                    }
                }
            }
            setLabel(prefix, obj, k);
            if (obj.containsKey("children"))
                setLabel(all, type, uri, prefix, obj.getJSONArray("children"), k);
        }
    }

    private void setLabel(String[] prefix, JSONObject object, String[] key) {
        if (json.hasTrue(object, "labeled"))
            return;

        object.put("labeled", true);
        if (object.containsKey("labels")) {
            if (object.get("labels") instanceof String) {
                JSONArray array = new JSONArray();
                array.addAll(Arrays.asList(converter.toArray(getMessage(prefix[0], object.getString("labels")), ",")));
                object.put("labels", array);
            }
        } else if (object.containsKey("values") && (object.get("values") instanceof String values)) {
            String string = getMessage(prefix[0], values);
            char ch = string.charAt(0);
            if (ch == '{')
                object.put("values", json.toObject(string));
            else if (ch == '[')
                object.put("values", json.toArray(string));
            else
                object.put("values", string);
        }
        if (object.containsKey("message"))
            object.put("message", getMessage(prefix[0], object.getString("message")));

        for (String k : key) {
            if (!object.containsKey(k))
                continue;

            for (String p : prefix) {
                String label = object.getString(k);
                String message = getMessage(p, label);
                if (!message.endsWith(label)) {
                    object.put("label", message);

                    return;
                }
            }
        }
        object.put("label", "");
    }

    private String getMessage(String prefix, String key) {
        int index = key.indexOf('.');
        if (index == -1)
            key = prefix + "." + key;
        else if (index == 0)
            key = prefix + key;

        return message.get((key));
    }

    private JSONArray props(JSONObject meta, JSONObject m, String name, String uri, String service) {
        JSONArray props = props(meta, m, name);
        if (declares.containsKey(uri)) {
            JSONArray array = declares.get(uri).getProps(service);
            if (!validator.isEmpty(array)) {
                Map<String, JSONObject> map = new HashMap<>();
                for (int i = 0, size = props.size(); i < size; i++) {
                    JSONObject prop = props.getJSONObject(i);
                    map.put(prop.getString("name"), prop);
                }

                JSONArray ps = new JSONArray();
                for (int i = 0, size = array.size(); i < size; i++) {
                    JSONObject object = array.getJSONObject(i);
                    JSONObject p = new JSONObject();
                    String n = object.getString("name");
                    if (map.containsKey(n))
                        p.putAll(map.get(n));
                    p.putAll(object);
                    ps.add(p);
                }
                props = ps;
            }
        }

        return props;
    }

    @Override
    public JSONArray props(JSONObject meta, JSONObject m) {
        return props(meta, m, "props");
    }

    private JSONArray props(JSONObject meta, JSONObject m, String name) {
        if (!json.containsKey(m, name))
            return json.containsKey(meta, "props") ? meta.getJSONArray("props") : null;

        JSONArray props = m.getJSONArray(name);
        if (!json.containsKey(meta, "props"))
            return props;

        JSONArray array = new JSONArray();
        JSONArray mps = meta.getJSONArray("props");
        for (int i = 0, size = props.size(); i < size; i++) {
            JSONObject prop = props.getJSONObject(i);
            if (prop.containsKey("name")) {
                for (int j = 0, s = mps.size(); j < s; j++) {
                    JSONObject mp = mps.getJSONObject(j);
                    if (prop.getString("name").equals(mp.getString("name"))) {
                        JSONObject object = json.copy(mp);
                        object.putAll(prop);
                        if (prop.containsKey("label"))
                            object.remove("labeled");
                        prop = object;

                        break;
                    }
                }
            }
            array.add(prop);
        }

        return array;
    }

    @Override
    public int getContextRefreshedSort() {
        return 191;
    }

    @Override
    public void onContextRefreshed() {
        BeanFactory.getBeans(MetaDeclare.class).forEach(declare -> declares.put(declare.getUri(), declare));
        modelTables.getModelClasses().forEach(modelClass -> {
            try (InputStream inputStream = modelClass.getResourceAsStream("meta.json");
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                if (inputStream == null)
                    return;

                io.copy(inputStream, outputStream);
                put(outputStream.toString());
            } catch (Throwable throwable) {
                logger.warn(throwable, "解析Model[{}]元数据时发生异常！", modelClass);
            }
        });
        meta(new File(context.getAbsolutePath(console + "meta")).listFiles());
    }

    private void meta(File[] files) {
        if (files == null)
            return;

        for (File file : files) {
            if (file.isDirectory()) {
                meta(file.listFiles());

                continue;
            }

            if (!file.isFile() || !file.getName().endsWith(".json"))
                continue;

            put(io.readAsString(file.getAbsolutePath()));
        }
    }

    private void put(String string) {
        JSONObject object = json.toObject(string);
        if (object == null)
            return;

        String prefix = object.getString("key") + ".";
        if (object.containsKey("props"))
            upload(prefix, object);
        for (String key : object.keySet()) {
            if (kup.contains(key))
                continue;

            JSONObject obj = object.getJSONObject(key);
            if (json.containsKey(obj, "props"))
                upload(prefix, obj);
            if (json.containsKey(obj, "toolbar"))
                upload(prefix, "upload", obj.getJSONArray("toolbar"));
            else if (json.has(object, "key", "setting")) {
                JSONObject save = new JSONObject();
                save.put("label", "save");
                save.put("service", "/keyvalue/saves");
                JSONArray toolbar = new JSONArray();
                toolbar.add(save);
                obj.put("toolbar", toolbar);
            }
        }

        string = object.toJSONString();
        if (object.containsKey("key"))
            map.put(object.getString("key"), string);
        if (object.containsKey("uri"))
            map.put(object.getString("uri"), string);
    }

    private void upload(String prefix, JSONObject object) {
        JSONArray props = object.getJSONArray("props");
        upload(prefix, "image", props);
        upload(prefix, "file", props);
    }

    private void upload(String prefix, String type, JSONArray array) {
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject prop = array.getJSONObject(i);
            if (json.has(prop, "type", type)) {
                String upload = prop.containsKey("upload") ? prop.getString("upload") : null;
                if (upload == null)
                    prop.put("upload", prefix + prop.getString("name"));
                else if (upload.indexOf('.') == -1)
                    prop.put("upload", prefix + upload);
            }
            if (json.has(prop, "type", "array"))
                upload(prefix, type, prop.getJSONArray("children"));
        }
    }

    @Override
    public void crosierValid(int grade) {
        if (cacheKeys.containsKey(grade))
            cacheKeys.remove(grade).forEach(cache::remove);
    }
}
