package org.lpw.clivia.user.illegal;

import com.alibaba.fastjson.JSONObject;

public interface IllegalService {
    JSONObject query();

    void save(String user);
}
