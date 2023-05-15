package com.desert.eagle.player.profit;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Date;
import java.util.Map;
import java.util.Set;

interface ProfitDao {
    PageList<ProfitModel> query(Set<String> player, String game, String date, int pageSize, int pageNum);

    PageList<ProfitModel> query(String player, String game, int pageSize, int pageNum);

    PageList<ProfitModel> query(String player, Date date);

    PageList<ProfitModel> query(Date date);

    PageList<ProfitModel> queryAllGame(String game, Date date, int pageSize, int pageNum);

    ProfitModel find(String player, String game, Date date);

    ProfitModel findById(String id);

    PageList<ProfitModel> water(String game);

    Map<String, int[]> water(String game, Date start, Date end);

    void save(ProfitModel profit);

    void delete(Date date);
}