package com.desert.eagle.player.ledger;

import com.alibaba.fastjson.JSONObject;

public interface LedgerService {
    JSONObject query(String uid, String nick, String invitor, String type, String time);

    JSONObject query(String player, String type, String time);

    JSONObject water(String time);

    void save(String player, String type, int balance0, int amount, int balance, String memo);

    JSONObject user();
}
