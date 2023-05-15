package com.desert.eagle.player;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;

public interface PlayerService {
    String VALIDATOR_UID_EXISTS = PlayerModel.NAME + ".uid.exists";

    JSONObject query(boolean junior, String uid, String nick, String memo, int ban, String time, Date date);

    Set<String> subids(String invitorUid);

    JSONObject subquery(String id);

    void inviter(String code);

    JSONObject sign();

    PlayerModel findById(String id);

    JSONObject get(String id);

    PlayerModel find(String uid);

    JSONObject getNickAvatar(String id);

    JSONArray junior();

    boolean deposit(String id, int amount);

    void gift(String id, int amount);

    void memo(String id, String memo);

    void deposit(String id, String type, int amount, String memo);

    boolean withdraw(String id, int amount, String memo);

    boolean bet(String id, int amount, String memo);

    void unbet(String id, int amount, String memo);

    void win(String id, int amount, String memo);

    void collect(String id, int amount, String type, String memo);

    JSONObject qr(String host);

    void save(String mobile);

    void ip(String ip);

    void ban(String id, int ban);

    boolean noEnough(String id, int amount);

    void setBetProfit(String id, int bet, int profit);

    int balance();

    int register(Timestamp start, Timestamp end);

    int balance(String id);

    void transfer();

    void transfer(String id, int amount, boolean pass);

    Set<String> ids(String uid, String nick, String invitorUid);

    void commission(Map<String, Integer> map);

    void invite(String id, String invitor);

    boolean hasInvitor(String id);
}
