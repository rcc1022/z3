package org.lpw.clivia.push;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface PushDao {
    PageList<PushModel> query(String scene, String sender, String name, int state, int pageSize, int pageNum);

    PushModel findById(String id);

    PushModel find(String scene, int state);

    void insert(PushModel push);

    void save(PushModel push);

    void state(String id, int state);

    void time(String id, Timestamp time);

    void delete(String id);
}
