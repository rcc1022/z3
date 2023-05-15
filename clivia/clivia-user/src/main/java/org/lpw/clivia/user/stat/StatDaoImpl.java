package org.lpw.clivia.user.stat;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Date;

@Repository(StatModel.NAME + ".dao")
class StatDaoImpl implements StatDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<StatModel> query(String date, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .between("c_date", ColumnType.Date, date)
                .order("c_date desc")
                .query(StatModel.class, pageSize, pageNum);
    }

    @Override
    public StatModel find(Date date) {
        return liteOrm.findOne(new LiteQuery(StatModel.class).where("c_date=?"), new Object[]{date});
    }

    @Override
    public void save(StatModel stat) {
        liteOrm.save(stat);
    }
}
