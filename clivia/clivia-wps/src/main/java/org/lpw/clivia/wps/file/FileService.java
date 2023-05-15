package org.lpw.clivia.wps.file;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface FileService {
    enum Permission {ReadOnly, ReadWrite}

    String preview(String key, String uri, String name, Permission permission, String creator, long create);

    void delete(String wps);

    JSONObject info(String id, Map<String, String> map);

    JSONObject user(String id, Map<String, String> map, String body);
}
