package org.lpw.clivia.olcs;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface OlcsDao {
    PageList<OlcsModel> query(String user, int pageSize, int pageNum);

    PageList<OlcsModel> query(String user, Timestamp time);

    OlcsModel findById(String id);

    void save(OlcsModel olcs);

    void delete(String user);

    void delete(Timestamp time);
}