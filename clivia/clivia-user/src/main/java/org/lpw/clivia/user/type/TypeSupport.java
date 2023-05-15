package org.lpw.clivia.user.type;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.util.Json;

import javax.inject.Inject;

public abstract class TypeSupport implements Type {
    @Inject
    protected Json json;
    @Inject
    protected Request request;

    @Override
    public String getMobile(String uid, String password) {
        return null;
    }

    @Override
    public String getEmail(String uid, String password) {
        return null;
    }

    @Override
    public String getNick(String uid, String password) {
        return null;
    }

    @Override
    public String getAvatar(String uid, String password) {
        return null;
    }

    @Override
    public String getFrom(String uid, String password) {
        return null;
    }

    protected String get(String uid, String password, String name) {
        JSONObject object = getAuth(uid, password);

        return json.containsKey(object, name) ? object.getString(name) : null;
    }

    @Override
    public JSONObject getAuth(String uid, String password) {
        return null;
    }

    protected String getInvitecode() {
        return request.get("invitecode");
    }
}
