package com.desert.eagle.football;

import org.lpw.photon.dao.orm.PageList;

interface FootballDao {
    PageList<FootballModel> query(int group, String league, String teamH, String teamC, int on, int sort, int pageSize, int pageNum);

    PageList<FootballModel> overdue(int group, int sort, long timestamp);

    PageList<FootballModel> over(int sort, long start, long end);

    FootballModel findById(String id);

    FootballModel find(int group, String gid);

    void save(FootballModel football);

    void on(String id, int on);

    void ons(int on, int sort);

    void delete(String id);

    void delete(long timestemp);
}