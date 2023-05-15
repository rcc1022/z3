package org.lpw.clivia.chrome;

import org.lpw.photon.dao.orm.PageList;

interface ChromeDao {
    PageList<ChromeModel> query(String key, String name, int pageSize, int pageNum);

    ChromeModel findByKey(String key);

    void save(ChromeModel chrome);

    void delete(String id);
}
