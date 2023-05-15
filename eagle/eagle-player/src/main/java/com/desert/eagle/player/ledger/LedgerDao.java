package com.desert.eagle.player.ledger;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;
import java.util.Set;

interface LedgerDao {
    PageList<LedgerModel> query(Set<String> player, String type, String time, int pageSize, int pageNum);

    void save(LedgerModel ledger);

    void delete(Timestamp time);
}