package com.desert.eagle.player.withdraw;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface WithdrawDao {
    PageList<WithdrawModel> query(String player, int type, int status, String submit, int pageSize, int pageNum);

    PageList<WithdrawModel> query(String player, int pageSize, int pageNum);

    int count(String player, Timestamp start, Timestamp end);

    WithdrawModel findById(String id);

    WithdrawModel find(String player, int status);

    int count(int status);

    int sum(Timestamp start, Timestamp end, int status);

    int sum(String player, Timestamp start, Timestamp end, int status);

    void save(WithdrawModel withdraw);

    void delete(Timestamp submit);
}