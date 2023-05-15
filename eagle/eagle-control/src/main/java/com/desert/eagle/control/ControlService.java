package com.desert.eagle.control;

import com.alibaba.fastjson.JSONObject;

public interface ControlService {
    JSONObject query(int mode);

    ControlModel find(String type);

    void save(ControlModel control);

    void profit(int type, int profit);

    void delete(String id);
}
