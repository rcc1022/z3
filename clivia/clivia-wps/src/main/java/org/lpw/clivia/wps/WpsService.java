package org.lpw.clivia.wps;

import com.alibaba.fastjson.JSONObject;

public interface WpsService {
    String VALIDATOR_KEY_NOT_EXISTS = WpsModel.NAME + ".validator.key.not-exists";

    JSONObject query(String key, String name, String appId);

    WpsModel findById(String id);

    WpsModel findByKey(String key);

    void save(WpsModel wps);

    void delete(String id);
}
