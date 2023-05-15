package org.lpw.clivia.user.upload;

import com.alibaba.fastjson.JSONObject;

public interface UploadService {
    JSONObject query();

    JSONObject user();

    void save(String id, String filename);

    void delete(String id);
}
