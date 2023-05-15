package com.desert.eagle.player.unwater;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface UnwaterDao {
    PageList<UnwaterModel> query(String time, int pageSize, int pageNum);

    UnwaterModel findById(String id);

    void save(UnwaterModel unwater);
}