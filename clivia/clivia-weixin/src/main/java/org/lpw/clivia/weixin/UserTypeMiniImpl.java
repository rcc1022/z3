package org.lpw.clivia.weixin;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.context.Request;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(WeixinModel.NAME + ".user-type.mini")
public class UserTypeMiniImpl extends UserTypeSupport {
    @Inject
    private Request request;

    @Override
    public String getKey() {
        return "weixin-mini";
    }

    @Override
    public String getMobile(String uid, String password) {
        return get(uid, password, "phoneNumber");
    }

    @Override
    public String getNick(String uid, String password) {
        return get(uid, password, "nickName");
    }

    @Override
    public String getAvatar(String uid, String password) {
        return get(uid, password, "avatarUrl");
    }

    @Override
    public JSONObject getAuth(String uid, String password) {
        String key = "ranch.user.type.weixin-mini.uid-password:" + uid + "-" + password;
        JSONObject object = context.getThreadLocal(key);
        if (object == null)
            context.putThreadLocal(key, object = weixinService.auth(password, uid, request.get("iv"), request.get("message"),
                    request.get("iv2"), request.get("message2")));

        return object;
    }
}
