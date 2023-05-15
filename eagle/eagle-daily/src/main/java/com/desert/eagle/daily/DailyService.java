package com.desert.eagle.daily;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface DailyService {
    JSONObject query(String game, String date);

    JSONObject today(boolean shortcut);

    JSONArray day7();
}
