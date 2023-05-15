package org.lpw.clivia.wps;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(WpsModel.NAME + ".dao")
class WpsDaoImpl implements WpsDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<WpsModel> query(String key, String name, String appId, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_key", DaoOperation.Equals, key)
                .where("c_app_id", DaoOperation.Equals, appId)
                .like(null, "c_name", name)
                .query(WpsModel.class, pageSize, pageNum);
    }

    @Override
    public WpsModel findById(String id) {
        return liteOrm.findById(WpsModel.class, id);
    }

    @Override
    public WpsModel findByKey(String key) {
        return liteOrm.findOne(new LiteQuery(WpsModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void save(WpsModel wps) {
        liteOrm.save(wps);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(WpsModel.class, id);
    }
}
