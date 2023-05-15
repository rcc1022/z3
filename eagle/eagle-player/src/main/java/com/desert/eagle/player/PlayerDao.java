package com.desert.eagle.player;

import org.lpw.photon.dao.jdbc.SqlTable;
import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;
import java.util.Set;

interface PlayerDao {
    PageList<PlayerModel> query(Set<String> player, String uid, String memo, int ban, String time, boolean junior, int pageSize, int pageNum);

    PageList<PlayerModel> query(String invitor, int pageSize, int pageNum);

    PageList<PlayerModel> query(String invitor);

    PlayerModel findById(String id);

    PlayerModel find(String uid);

    PlayerModel maxUid();

    int balance();

    int count(Timestamp start, Timestamp end);

    void insert(PlayerModel player);

    void save(PlayerModel player);

    void ban(String id, int ban);

    void zero();

    int junior(String invter);

    SqlTable countInvite();

    void clearQrcode();

    void delete(String id);
}