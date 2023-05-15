package com.desert.eagle.football;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface FootballService {
    JSONObject query(int group, String league, String teamH, String teamC, int on);

    JSONObject get(int group, String gid);

    FootballModel find(int group, String gid);

    void save(FootballModel football);

    void saves(int group, JSONArray games, JSONArray bodans);

    void on(String id, int on);

    void allOnOff(int on);

    void delete(String id);

    JSONArray ons(int group);

    String rate(int group, String gid, String type, String team);

    boolean check(String gid, String type, String team, String rate);

    JSONObject failure();
}
