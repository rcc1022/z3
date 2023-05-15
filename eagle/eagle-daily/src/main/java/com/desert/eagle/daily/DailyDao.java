package com.desert.eagle.daily;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Date;

interface DailyDao {
    PageList<DailyModel> query(String game, String date, int pageSize, int pageNum);

    PageList<DailyModel> query(String game, Date start, Date end);

    DailyModel find(Date date, String game);

    void save(DailyModel daily);

    void delete(Date date);
}