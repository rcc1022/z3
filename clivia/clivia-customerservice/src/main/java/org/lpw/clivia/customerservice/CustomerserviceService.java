package org.lpw.clivia.customerservice;

import com.alibaba.fastjson.JSONObject;

public interface CustomerserviceService {
    JSONObject query(String type, int state);

    JSONObject one(String type);

    void save(CustomerserviceModel customerservice);

    void state(String id, int state);

    void delete(String id);
}
