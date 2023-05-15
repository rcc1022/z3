package org.lpw.clivia.device;

import com.alibaba.fastjson.JSONObject;

public interface DeviceService {
    JSONObject query();

    DeviceModel find(String sid);

    void save(String type, String identifier, String lang);
}
