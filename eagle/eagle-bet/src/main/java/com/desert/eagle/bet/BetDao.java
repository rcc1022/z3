package com.desert.eagle.bet;

import org.lpw.photon.dao.jdbc.SqlTable;
import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;
import java.util.List;

interface BetDao {
    PageList<BetModel> query(String game, String player, String issue, int win, String time, int pageSize, int pageNum);

    long sumAmount(int status, int robot, Map<String, String> map);

    PageList<BetModel> query(String game, String issue, int status);
    List<BetModel> queryUserBetList(String game, String issue);

    PageList<BetModel> query(String issue, int status, int robot);

    PageList<BetModel> query(String game, String player, String issue, int status);

    PageList<BetModel> query(int status, String zhuihao);

    BetModel findById(String id);

    int sum(String game, String issue, int status, int robot);

    SqlTable sum(int status, int robot, Timestamp start, Timestamp end);

    SqlTable sum(Set<String> player, String game, int status, int robot, Timestamp start, Timestamp end);

    SqlTable sum(String game, String issue, int robot);

    int count(String zhuihao);

    void save(BetModel bet);

    void delete(BetModel bet);

    void delete(int status, Timestamp settle);

    void deleteRobot(int robot, Timestamp time);

    void clear(String game,String issue,int status);

    void close();
}