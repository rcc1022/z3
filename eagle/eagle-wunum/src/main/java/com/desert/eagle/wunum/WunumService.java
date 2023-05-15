package com.desert.eagle.wunum;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.GameModel;

import java.util.Map;

public interface WunumService {
    JSONObject query(int type, String issue);

    JSONObject latest(String game);

    JSONArray list(int type);

    Map<String, String> nextGameIssue(String game);

    boolean close(GameModel game, long issue);

    JSONObject get(int type, long issue);

    void save(WunumModel wunum);

    void open(String id);

    void delete(String id);

    JSONObject getLotteryPksInfo();
}
