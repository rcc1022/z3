package com.desert.eagle.player.brokerage;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Date;

interface BrokerageDao {
    PageList<BrokerageModel> query(Date date);

    BrokerageModel find(String player, Date date);

    void save(BrokerageModel brokerage);
}