package com.desert.eagle.player.withdraw;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.player.PlayerModel;

import java.sql.Timestamp;

public interface WithdrawService {
    String TIME_RANGE_VALIDATOR = WithdrawModel.NAME + ".validator.time-range";

    JSONObject query(String uid, int type, int status, String time, int timeN);

    JSONObject user();

    int surplus();

    int submit(int type, int amount);

    void pass(String id);

    void reject(String id);

    JSONObject newer();

    boolean save(PlayerModel player, int amount);

    int sum(Timestamp start, Timestamp end);

    int sum(String player, Timestamp start, Timestamp end);
}
