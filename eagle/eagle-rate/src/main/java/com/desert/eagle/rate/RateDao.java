package com.desert.eagle.rate;

import org.lpw.photon.dao.orm.PageList;

interface RateDao {
    PageList<RateModel> query(String game, String type, int pageSize, int pageNum);

    RateModel findById(String id);

    RateModel find(String game, String type, String name);

    void save(RateModel rate);

    void delete(String game);
}