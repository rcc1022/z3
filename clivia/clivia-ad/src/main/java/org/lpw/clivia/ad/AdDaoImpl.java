package org.lpw.clivia.ad;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.util.Set;

@Repository(AdModel.NAME + ".dao")
class AdDaoImpl implements AdDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<AdModel> query(String type, int state, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_type", DaoOperation.Equals, type)
                .where("c_state", DaoOperation.Equals, state)
                .order("c_state desc,c_type,c_sort")
                .query(AdModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<AdModel> query(Set<String> type, int state) {
        return daoHelper.newQueryBuilder().in("c_type", type)
                .where("c_state", DaoOperation.Equals, state)
                .order("c_sort")
                .query(AdModel.class, 0, 0);
    }

    @Override
    public AdModel findById(String id) {
        return liteOrm.findById(AdModel.class, id);
    }

    @Override
    public void save(AdModel ad) {
        liteOrm.save(ad);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(AdModel.class, id);
    }
}
