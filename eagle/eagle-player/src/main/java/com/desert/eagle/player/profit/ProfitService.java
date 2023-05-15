package com.desert.eagle.player.profit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.Date;
import java.util.List;
import java.util.Map;

public interface ProfitService {
    JSONObject query(String uid, String nick, String invitor, String date);

    JSONObject today();

    List<ProfitModel> list(Date date);

    List<ProfitModel> allGame(Date date);

    JSONObject user();

    JSONObject junior(String player);

    ProfitModel find(String player, Date date);

    void save(String game, String player, Date date, int count, int amount, int profit, int commission);

    void water(Date date, Map<String, Integer> map);

    Map<String, int[]> water(Date start, Date end);

    void deposit(String player, int amount);

    void withdraw(String player, int amount);

    JSONArray towater();

    void passAll();

    void pass(String id);

    void reject(String id);
}
