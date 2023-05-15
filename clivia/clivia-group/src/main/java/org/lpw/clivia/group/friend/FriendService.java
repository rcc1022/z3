package org.lpw.clivia.group.friend;

import com.alibaba.fastjson.JSONObject;

public interface FriendService {
    JSONObject user();

    FriendModel find(String user1, String user2);

    void add(String user, String memo);

    void agree(String id);

    void reject(String id);

    void delete(String user1, String user2);
}
