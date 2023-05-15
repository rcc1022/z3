package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;

public interface EditorListener {
    void save(String key, JSONArray array);
}
