package org.lpw.clivia.olcs.member;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public interface MemberService {
    JSONObject query(int size);

    JSONObject user();

    JSONObject unread();

    MemberModel findById(String id);

    void save(String id, String content, boolean reply);

    void userRead(String id, Timestamp time);

    void replierRead(String id, Timestamp time);

    void clean(String id);

    void empty(Timestamp time);
}
