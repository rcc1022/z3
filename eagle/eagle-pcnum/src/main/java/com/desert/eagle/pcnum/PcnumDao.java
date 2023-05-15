package com.desert.eagle.pcnum;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface PcnumDao {
    PageList<PcnumModel> query(int type, String issue, int pageSize, int pageNum);

    PcnumModel findById(String id);

    PcnumModel find(int type, long issue);

    PcnumModel latest(int type, int status);

    void save(PcnumModel pcnum);

    void delete(String id);

    void delete(Timestamp time);
}