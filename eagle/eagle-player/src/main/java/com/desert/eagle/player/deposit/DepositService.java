package com.desert.eagle.player.deposit;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.player.PlayerModel;

import java.sql.Timestamp;

public interface DepositService {
    JSONObject query(String uid, String nick, int status, String time, int timeN);

    JSONObject user();

    void save(String uid, int amount);

    void save(PlayerModel player, boolean gift, int amount);

    int submit(String type, int amount);

    void pass(String id);

    void reject(String id);

    JSONObject newer();

    int sum(boolean gift, Timestamp start, Timestamp end);

    int sum(String player, boolean gift, Timestamp start, Timestamp end);
}
