package org.lpw.clivia.pair;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(PairModel.NAME + ".dao")
class PairDaoImpl implements PairDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<PairModel> query(String owner) {
        return liteOrm.query(new LiteQuery(PairModel.class).where("c_owner=?"), new Object[] { owner });
    }

    @Override
    public PageList<PairModel> query(String owner, boolean desc, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(PairModel.class).where("c_owner=?")
                .order("c_time " + (desc ? "desc" : "asc")).size(pageSize).page(pageNum), new Object[] { owner });
    }

    @Override
    public int count(String owner, String value) {
        return liteOrm.count(new LiteQuery(PairModel.class).where("c_owner=? and c_value=?"),
                new Object[] { owner, value });
    }

    @Override
    public int count(String owner) {
        return liteOrm.count(new LiteQuery(PairModel.class).where("c_owner=?"), new Object[] { owner });
    }

    @Override
    public PairModel find(String owner, String value) {
        return liteOrm.findOne(new LiteQuery(PairModel.class).where("c_owner=? and c_value=?"),
                new Object[] { owner, value });
    }

    @Override
    public void save(PairModel pair) {
        liteOrm.save(pair);
    }

    @Override
    public void delete(String owner, String value) {
        liteOrm.delete(new LiteQuery(PairModel.class).where("c_owner=? and c_value=?"), new Object[] { owner, value });
    }
}