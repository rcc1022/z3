package com.desert.eagle.player.ledger;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Set;

@Repository(LedgerModel.NAME + ".dao")
class LedgerDaoImpl implements LedgerDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<LedgerModel> query(Set<String> player, String type, String time, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .in("c_player", player)
                .where("c_type", DaoOperation.Equals, type)
                .between("c_time", ColumnType.Timestamp, time)
                .order("c_time desc,c_timestamp desc")
                .query(LedgerModel.class, pageSize, pageNum);
    }

    @Override
    public void save(LedgerModel ledger) {
        liteOrm.save(ledger);
    }

    @Override
    public void delete(Timestamp time) {
        liteOrm.delete(new LiteQuery(LedgerModel.class).where("c_time<?"), new Object[]{time});
    }
}