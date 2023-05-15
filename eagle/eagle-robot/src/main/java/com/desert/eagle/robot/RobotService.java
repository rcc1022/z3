package com.desert.eagle.robot;

import com.alibaba.fastjson.JSONObject;

public interface RobotService {
    JSONObject query();

    void save(RobotModel robot);

    void allot();

    void delete(String id);
}
