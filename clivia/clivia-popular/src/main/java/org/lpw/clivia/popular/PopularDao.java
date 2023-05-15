package org.lpw.clivia.popular;

import org.lpw.photon.dao.orm.PageList;

interface PopularDao {
    PageList<PopularModel> query(String key, int pageSize, int pageNum);

    PageList<PopularModel> query(String key, int state, int pageSize, int pageNum);

    PopularModel findById(String id);

    PopularModel find(String key, String value);

    void save(PopularModel popular);
}
