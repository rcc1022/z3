package org.lpw.clivia.module;

import org.lpw.photon.dao.orm.PageList;

interface ModuleDao {
    PageList<ModuleModel> query(int pageSize, int pageNum);

    ModuleModel findById(String id);

    void save(ModuleModel module);

    void delete(String id);
}
