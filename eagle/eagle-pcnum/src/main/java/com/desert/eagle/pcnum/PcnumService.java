package com.desert.eagle.pcnum;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.GameModel;

import java.util.Map;

public interface PcnumService {

    JSONObject query(int type, String issue);

    JSONObject latest(String game);

    Map<String, String> nextGameIssue(String game);

    JSONArray list(int type);

    boolean close(GameModel game, long issue);

    JSONObject get(int type, long issue);

    void save(PcnumModel pcnum);

    void open(String id);

    void delete(String id);
}
