package com.desert.eagle.message;

import com.alibaba.fastjson.JSONObject;

public interface MessageService {
    int open = 0;
    int close = 1;
    int bet = 2;
    int bets = 3;

    JSONObject query(String game, long time);

    void save(String game, String player, int type, String content);
}
