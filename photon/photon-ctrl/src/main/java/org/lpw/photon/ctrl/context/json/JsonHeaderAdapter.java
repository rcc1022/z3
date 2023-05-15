package org.lpw.photon.ctrl.context.json;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.context.HeaderAdapter;

public class JsonHeaderAdapter extends Support implements HeaderAdapter {
    private String ip;

    public JsonHeaderAdapter(JSONObject object, String ip) {
        super(object);
        this.ip = ip;
    }

    @Override
    public String getIp() {
        return ip;
    }
}
