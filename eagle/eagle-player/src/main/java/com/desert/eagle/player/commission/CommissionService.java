package com.desert.eagle.player.commission;

import com.alibaba.fastjson.JSONObject;

public interface CommissionService {
    JSONObject query();

    void save(String player, int amount);

    void pass(String id);

    void reject(String id);
}
