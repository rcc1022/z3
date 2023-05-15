package org.lpw.clivia.popular;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(PopularModel.NAME + ".dao")
class PopularDaoImpl implements PopularDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<PopularModel> query(String key, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(PopularModel.class).where("c_key=?").order("c_state,c_count desc")
                .size(pageSize).page(pageNum), new Object[]{key});
    }

    @Override
    public PageList<PopularModel> query(String key, int state, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(PopularModel.class).where("c_key=? and c_state=?").order("c_count desc")
                .size(pageSize).page(pageNum), new Object[]{key, state});
    }

    @Override
    public PopularModel findById(String id) {
        return liteOrm.findById(PopularModel.class, id);
    }

    @Override
    public PopularModel find(String key, String value) {
        return liteOrm.findOne(new LiteQuery(PopularModel.class).where("c_key=? and c_value=?"), new Object[]{key, value});
    }

    @Override
    public void save(PopularModel popular) {
        liteOrm.save(popular);
    }
}
