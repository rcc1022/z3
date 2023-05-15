package org.lpw.clivia.alipay;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(AlipayModel.NAME + ".dao")
class AlipayDaoImpl implements AlipayDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<AlipayModel> query() {
        return liteOrm.query(new LiteQuery(AlipayModel.class), null);
    }

    @Override
    public AlipayModel findByKey(String key) {
        return liteOrm.findOne(new LiteQuery(AlipayModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public AlipayModel findByAppId(String appId) {
        return liteOrm.findOne(new LiteQuery(AlipayModel.class).where("c_app_id=?"), new Object[]{appId});
    }

    @Override
    public void save(AlipayModel alipay) {
        liteOrm.save(alipay);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(AlipayModel.class, id);
    }
}
