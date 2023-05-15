package com.desert.eagle.game;

import org.lpw.photon.dao.orm.PageList;

interface GameDao {
    PageList<GameModel> query(int on, int pageSize, int pageNum);

    GameModel findById(String id);

    GameModel findByType(int type);

    void save(GameModel game);

    void on(String id, int on);

    void delete(String id);

    void close();
}