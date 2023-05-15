package org.lpw.clivia.wps;

import org.lpw.photon.dao.orm.PageList;

interface WpsDao {
    PageList<WpsModel> query(String key, String name, String appId, int pageSize, int pageNum);

    WpsModel findById(String id);

    WpsModel findByKey(String key);

    void save(WpsModel wps);

    void delete(String id);
}
