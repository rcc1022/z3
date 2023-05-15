package org.lpw.clivia.pair;

import org.lpw.photon.dao.orm.PageList;

interface PairDao {
    PageList<PairModel> query(String owner);

    PageList<PairModel> query(String owner, boolean desc, int pageSize, int pageNum);

    int count(String owner, String value);

    int count(String owner);

    PairModel find(String owner, String value);

    void save(PairModel pair);

    void delete(String owner, String value);
}