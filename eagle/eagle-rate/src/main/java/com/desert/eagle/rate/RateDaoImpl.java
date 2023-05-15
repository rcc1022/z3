package com.desert.eagle.rate;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(RateModel.NAME + ".dao")
class RateDaoImpl implements RateDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<RateModel> query(String game, String type, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_game", DaoOperation.Equals, game)
                .where("c_type", DaoOperation.Equals, type)
                .order("c_game,c_type,c_sort")
                .query(RateModel.class, pageSize, pageNum);
    }

    @Override
    public RateModel findById(String id) {
        return liteOrm.findById(RateModel.class, id);
    }

    @Override
    public RateModel find(String game, String type, String name) {
        return liteOrm.findOne(new LiteQuery(RateModel.class).where("c_game=? and c_type=? and c_name=?"), new Object[]{game, type, name});
    }

    @Override
    public void save(RateModel rate) {
        liteOrm.save(rate);
    }

    @Override
    public void delete(String game) {
        liteOrm.delete(new LiteQuery(RateModel.class).where("c_game=?"), new Object[]{game});
    }
}