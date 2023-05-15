package com.desert.eagle.scnum;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface ScnumDao {
    PageList<ScnumModel> query(int type, String issue, int pageSize, int pageNum);

    ScnumModel findById(String id);

    ScnumModel find(int type, long issue);

    ScnumModel latest(int type, int status);

    void save(ScnumModel scnum);

    void delete(String id);

    void delete(Timestamp time);
}