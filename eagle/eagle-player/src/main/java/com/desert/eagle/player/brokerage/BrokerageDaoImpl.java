package com.desert.eagle.player.brokerage;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Date;

@Repository(BrokerageModel.NAME + ".dao")
class BrokerageDaoImpl implements BrokerageDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<BrokerageModel> query(Date date) {
        return liteOrm.query(new LiteQuery(BrokerageModel.class).where("c_date=?"), new Object[]{date});
    }

    @Override
    public BrokerageModel find(String player, Date date) {
        return liteOrm.findOne(new LiteQuery(BrokerageModel.class).where("c_date=? and c_player=?"), new Object[]{date, player});
    }

    @Override
    public void save(BrokerageModel brokerage) {
        liteOrm.save(brokerage);
    }
}