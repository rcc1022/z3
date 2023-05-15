package com.desert.eagle.game;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface GameService {
    JSONObject query(int on);

    List<GameModel> list();

    GameModel get(String id);

    GameModel find(int type);

    JSONObject json(String id);

    void save(GameModel game);

    void on(String id, int on);

    void delete(String id);

    String rule(String id);
}
