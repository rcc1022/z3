package org.lpw.clivia.module;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(ModuleModel.NAME + ".dao")
class ModuleDaoImpl implements ModuleDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<ModuleModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(ModuleModel.class).order("c_code desc").size(pageSize).page(pageNum), null);
    }

    @Override
    public ModuleModel findById(String id) {
        return liteOrm.findById(ModuleModel.class, id);
    }

    @Override
    public void save(ModuleModel module) {
        liteOrm.save(module);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(ModuleModel.class, id);
    }
}
