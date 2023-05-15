package org.lpw.clivia.upgrader;

import org.lpw.photon.dao.orm.PageList;

interface UpgraderDao {
    PageList<UpgraderModel> query(int pageSize, int pageNum);

    UpgraderModel findById(String id);

    UpgraderModel latest();

    void insert(UpgraderModel upgrader);

    void save(UpgraderModel upgrader);

    void delete(String id);
}
