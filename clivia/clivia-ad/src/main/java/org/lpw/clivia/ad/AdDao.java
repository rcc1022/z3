package org.lpw.clivia.ad;

import org.lpw.photon.dao.orm.PageList;

import java.util.Set;

interface AdDao {
    PageList<AdModel> query(String type, int state, int pageSize, int pageNum);

    PageList<AdModel> query(Set<String> type, int state);

    AdModel findById(String id);

    void save(AdModel ad);

    void delete(String id);
}
