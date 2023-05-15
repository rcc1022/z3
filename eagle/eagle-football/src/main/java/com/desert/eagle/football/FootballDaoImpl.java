package com.desert.eagle.football;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(FootballModel.NAME + ".dao")
class FootballDaoImpl implements FootballDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<FootballModel> query(int group, String league, String teamH, String teamC, int on, int sort, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_group", DaoOperation.Equals, group)
                .where("c_on", DaoOperation.Equals, on)
                .where("c_sort", DaoOperation.Less, sort)
                .like(null, "c_league", league)
                .like(null, "c_team_h", teamH)
                .like(null, "c_team_c", teamC)
                .order("c_on desc,c_sort")
                .query(FootballModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<FootballModel> overdue(int group, int sort, long timestamp) {
        return liteOrm.query(new LiteQuery(FootballModel.class).where("c_group=? and c_sort<? and c_timestamp<?"), new Object[]{group, sort, timestamp});
    }

    @Override
    public PageList<FootballModel> over(int sort, long start, long end) {
        return liteOrm.query(new LiteQuery(FootballModel.class).where("c_sort=? and c_timestamp between ? and ?"), new Object[]{sort, start, end});
    }

    @Override
    public FootballModel findById(String id) {
        return liteOrm.findById(FootballModel.class, id);
    }

    @Override
    public FootballModel find(int group, String gid) {
        return liteOrm.findOne(new LiteQuery(FootballModel.class).where("c_group=? and c_gid=?"), new Object[]{group, gid});
    }

    @Override
    public void save(FootballModel football) {
        liteOrm.save(football);
    }

    @Override
    public void on(String id, int on) {
        liteOrm.update(new LiteQuery(FootballModel.class).set("c_on=?").where("c_id=?"), new Object[]{on, id});
    }

    @Override
    public void ons(int on, int sort) {
        liteOrm.update(new LiteQuery(FootballModel.class).set("c_on=?").where("c_sort<?"), new Object[]{on, sort});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(FootballModel.class, id);
    }

    @Override
    public void delete(long timestemp) {
        liteOrm.delete(new LiteQuery(FootballModel.class).where("c_timestamp<?"), new Object[]{timestemp});
    }
}