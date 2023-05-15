package com.desert.eagle.player.unwater;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(UnwaterModel.NAME + ".dao")
class UnwaterDaoImpl implements UnwaterDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<UnwaterModel> query(String time, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .between("c_time", ColumnType.Date, time)
                .order("c_time DESC")
                .query(UnwaterModel.class, pageSize, pageNum);
    }

    @Override
    public UnwaterModel findById(String id) {
        return liteOrm.findById(UnwaterModel.class, id);
    }

    @Override
    public void save(UnwaterModel unwater) {
        liteOrm.save(unwater);
    }
}