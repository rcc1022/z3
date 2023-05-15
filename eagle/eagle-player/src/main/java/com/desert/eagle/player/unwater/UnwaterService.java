package com.desert.eagle.player.unwater;

import com.alibaba.fastjson.JSONObject;

import java.sql.Timestamp;

public interface UnwaterService {

    JSONObject query(String time);

    void save(String player,int amount);
}
