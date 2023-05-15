package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface EditorService {
    void put(String key, JSONArray array, EditorListener listener);

    JSONArray get(String key);

    JSONObject save(String key, String id, JSONArray lines, long sync);
}
