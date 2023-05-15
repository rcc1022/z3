package org.lpw.clivia.group.friend;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface FriendDao {
    PageList<FriendModel> query(String user, int pageSize, int pageNum);

    FriendModel findById(String id);

    FriendModel find(String user, String proposer);

    void save(FriendModel friend);

    void state(int oldState, int newState, Timestamp time);

    void delete(FriendModel friend);

    void delete(String user);
}