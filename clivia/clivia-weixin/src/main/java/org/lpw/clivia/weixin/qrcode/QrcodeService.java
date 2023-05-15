package org.lpw.clivia.weixin.qrcode;

import com.alibaba.fastjson.JSONObject;

public interface QrcodeService {
    JSONObject query(String key, String appId, String user, String name, String scene, String time);

    JSONObject find(String key, String user, String name);

    QrcodeModel create(String key, String user, String name, String scene);

    void delete(String id);
}
