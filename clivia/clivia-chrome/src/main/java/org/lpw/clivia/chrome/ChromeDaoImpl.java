package org.lpw.clivia.chrome;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(ChromeModel.NAME + ".dao")
class ChromeDaoImpl implements ChromeDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<ChromeModel> query(String key, String name, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().like(null, "c_key", key)
                .like(null, "c_name", name)
                .query(ChromeModel.class, pageSize, pageNum);
    }

    @Override
    public ChromeModel findByKey(String key) {
        return liteOrm.findOne(new LiteQuery(ChromeModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void save(ChromeModel chrome) {
        liteOrm.save(chrome);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(ChromeModel.class, id);
    }
}
