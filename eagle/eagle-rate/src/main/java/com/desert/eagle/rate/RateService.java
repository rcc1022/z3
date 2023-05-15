package com.desert.eagle.rate;

import com.alibaba.fastjson.JSONObject;

public interface RateService {

    JSONObject query(String game, String type);

    RateModel find(String game, String type, String name);

    JSONObject list(String game);

    void save(RateModel rate);

    void reset(String game);

    void saiChe(String game, int haoMa, int shuangMian);
}
