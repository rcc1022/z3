package com.desert.eagle.scnum;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.GameModel;

import java.util.Map;

public interface ScnumService {
    JSONObject query(int type, String issue);

    JSONObject latest(String game);

    JSONArray list(int type);

    Map<String, String> nextGameIssue(String game);

    boolean close(GameModel game, long issue);

    JSONObject get(int type, long issue);

    void save(ScnumModel scnum);

    void open(String id);

    void delete(String id);

    JSONObject getLotteryPksInfo();
}
