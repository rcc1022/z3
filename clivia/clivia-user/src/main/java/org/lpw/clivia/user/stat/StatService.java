package org.lpw.clivia.user.stat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface StatService {
    JSONObject query(String date);

    JSONObject today();

    JSONArray week();

    JSONArray pie();
}
