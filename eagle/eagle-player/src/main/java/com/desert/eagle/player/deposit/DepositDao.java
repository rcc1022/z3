package com.desert.eagle.player.deposit;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;
import java.util.Set;

interface DepositDao {
    PageList<DepositModel> query(Set<String> player, int status, String submit, int pageSize, int pageNum);

    PageList<DepositModel> query(String player, int pageSize, int pageNum);

    DepositModel findById(String id);

    int count(int status);

    DepositModel find(String player, int status);

    int sum(boolean gift, Timestamp start, Timestamp end, int status);

    int sum(String player, boolean gift, Timestamp start, Timestamp end, int status);

    void save(DepositModel deposit);

    void delete(Timestamp submit);
}