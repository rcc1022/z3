package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Validator;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Editing {
    private final Validator validator;
    private final Generator generator;
    private final String key;
    private String id;
    private List<String> ids;
    private long idTime;
    private final Map<String, JSONObject> lines;
    private long time;
    private final EditorListener listener;

    Editing(Validator validator, Generator generator, String key, JSONArray array, EditorListener listener) {
        this.generator = generator;
        this.validator = validator;
        this.key = key;
        id = "";
        ids = new ArrayList<>();
        idTime = System.currentTimeMillis();
        lines = new ConcurrentHashMap<>();
        time = System.currentTimeMillis();
        this.listener = listener;
        set(array);
    }

    private void set(JSONArray array) {
        if (validator.isEmpty(array)) {
            JSONObject object = new JSONObject();
            object.put("tag", "text");
            object.put("text", new JSONArray());
            ids.add(put(object));
        } else {
            for (int i = 0, size = array.size(); i < size; i++) {
                ids.add(put(array.getJSONObject(i)));
            }
        }
    }

    JSONObject put(String id, JSONArray lines, long sync) {
        JSONObject object = new JSONObject();
        object.put("sync", System.currentTimeMillis());

        Map<String, JSONObject> map = new HashMap<>();
        long idTime = 0;
        if (!lines.isEmpty()) {
            time = System.currentTimeMillis();
            for (int i = 0, size = lines.size(); i < size; i++) {
                JSONObject line = lines.getJSONObject(i);
                idTime = Math.max(idTime, line.getLongValue("time"));
                map.put(line.getString("id"), line);
            }
        }
        JSONArray array = new JSONArray();
        this.lines.forEach((key, value) -> {
            long vtime = value.getLongValue("time");
            if (map.containsKey(key)) {
                JSONObject line = map.remove(key);
                long ltime = line.getLongValue("time");
                if (ltime > vtime)
                    this.lines.put(key, line);
                else if (ltime < vtime)
                    array.add(value);
            } else if (vtime >= sync) {
                array.add(value);
            }
        });
        if (!map.isEmpty())
            this.lines.putAll(map);
        object.put("lines", array);
        if (this.id.equals(id))
            return object;

        if (this.idTime > idTime) {
            object.put("id", this.id);
        } else {
            this.idTime = idTime;
            this.id = id;
            List<String> list = new ArrayList<>();
            Collections.addAll(list, id.split(","));
            ids = list;
        }

        return object;
    }

    private String put(JSONObject object) {
        String id = object.getString("id");
        if (validator.isEmpty(id)) {
            id = id();
            object.put("id", id);
        }
        lines.put(id, object);

        return id;
    }

    private String id() {
        for (int i = 0; i < 1024; i++) {
            String id = generator.random(16);
            if (!lines.containsKey(id))
                return id;
        }

        return generator.random(16);
    }

    JSONArray get() {
        JSONArray array = new JSONArray();
        ids.forEach(id -> {
            if (lines.containsKey(id))
                array.add(lines.get(id));
        });

        return array;
    }

    void save(long time) {
        if (this.time > time && listener != null)
            listener.save(key, get());
    }

    boolean overdue(long time) {
        return this.time < time;
    }
}
