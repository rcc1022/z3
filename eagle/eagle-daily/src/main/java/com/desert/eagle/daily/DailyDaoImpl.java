package com.desert.eagle.daily;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Date;

@Repository(DailyModel.NAME + ".dao")
class DailyDaoImpl implements DailyDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<DailyModel> query(String game, String date, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_game", DaoOperation.Equals, game)
                .between("c_date", ColumnType.Date, date)
                .order("c_date DESC")
                .query(DailyModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<DailyModel> query(String game, Date start, Date end) {
        return liteOrm.query(new LiteQuery(DailyModel.class).where("c_game=? and c_date between ? and ?").order("c_date desc"), new Object[]{game, start, end});
    }

    @Override
    public DailyModel find(Date date, String game) {
        return liteOrm.findOne(new LiteQuery(DailyModel.class).where("c_date=? and c_game=?"), new Object[]{date, game});
    }

    @Override
    public void save(DailyModel daily) {
        liteOrm.save(daily);
        liteOrm.close();
    }

    @Override
    public void delete(Date date) {
        liteOrm.delete(new LiteQuery(DailyModel.class).where("c_date<?"), new Object[]{date});
    }
}