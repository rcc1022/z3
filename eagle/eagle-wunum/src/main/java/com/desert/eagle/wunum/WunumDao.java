package com.desert.eagle.wunum;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface WunumDao {
    PageList<WunumModel> query(int type, String issue, int pageSize, int pageNum);

    WunumModel findById(String id);

    WunumModel find(int type, long issue);

    WunumModel latest(int type, int status);

    void save(WunumModel wunum);

    void delete(String id);

    void delete(Timestamp time);
}