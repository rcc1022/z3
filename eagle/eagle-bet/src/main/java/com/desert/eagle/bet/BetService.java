package com.desert.eagle.bet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface BetService {
    JSONObject query(String id, String game, String uid, String issue, int win, String time);

    String immediately();

    JSONObject user(String game, String time);

    int save(String game, String issue, JSONArray items);

    void robot(String game, String robot);

    int cancel(String game);

    int zhuihao(String game, long issue, String type, String name, int amount, int count, int multiple, int stop);

    JSONObject sum1(String uid, String nick, String game, String time);

    JSONObject sum2(String uid, String nick, String invitor, String game, String time, int timeType);

    JSONObject total(String game);

    void clear(String game, String issue);
}
