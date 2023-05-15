package org.lpw.clivia.olcs;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public interface OlcsService {
    JSONObject query(String user, Timestamp time);

    JSONObject user(Timestamp time);

    void ask(String genre, String content);

    void reply(String user, String genre, String content);

    void delete(String id);

    void clean(String user);
}
