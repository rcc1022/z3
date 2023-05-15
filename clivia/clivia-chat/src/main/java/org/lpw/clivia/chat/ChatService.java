package org.lpw.clivia.chat;

import com.alibaba.fastjson.JSONObject;

import java.util.function.BiConsumer;

public interface ChatService {
    JSONObject query(String group, long time);

    JSONObject query(String group, long time, BiConsumer<ChatModel, JSONObject> consumer);

    void save(String group, String sender, String genre, String body);
}
