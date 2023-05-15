package com.desert.eagle.util;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.context.Session;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("eagle.util.parameter-helper")
public class ParameterHelperImpl implements ParameterHelper {
    @Inject
    private Request request;
    @Inject
    private Session session;

    @Override
    public String get(String key, String name) {
        String value = request.get(name);
        if (value == null)
            value = session.get(key + name);
        else
            session.set(key + name, value);

        return value;
    }

    @Override
    public String get(String key, String[] name) {
        JSONObject object = request.getAsJsonObject(name[0]);
        String k = key + name[0] + "." + name[1];
        if (object == null)
            return session.get(k);

        String value = object.getString(name[1]);
        if (value == null)
            value = session.get(k);
        else
            session.set(k, value);

        return value;
    }
}
