package org.lpw.clivia.push;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface PushService {
    String VALIDATOR_CAPTCHA = PushModel.NAME + ".validator.captcha";

    JSONObject query(String scene, String sender, String name, int state);

    JSONArray scenes();

    JSONArray senders();

    void save(PushModel push);

    void state(String id, int state);

    void delete(String id);

    Object send(String scene, JSONObject args);

    Object captcha(String scene, String mobile);

    boolean captcha(String code);
}
