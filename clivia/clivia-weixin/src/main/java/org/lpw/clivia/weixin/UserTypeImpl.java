package org.lpw.clivia.weixin;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;

@Service(WeixinModel.NAME + ".user-type")
public class UserTypeImpl extends UserTypeSupport {
    @Override
    public String getKey() {
        return "weixin";
    }

    @Override
    public String getNick(String uid, String password) {
        return get(uid, password, "nickname");
    }

    @Override
    public String getAvatar(String uid, String password) {
        return get(uid, password, "headimgurl");
    }

    @Override
    public JSONObject getAuth(String uid, String password) {
        String key = "ranch.user.type.weixin.uid-password:" + uid + "-" + password;
        JSONObject object = context.getThreadLocal(key);
        if (object == null)
            context.putThreadLocal(key, object = weixinService.auth(password, uid));

        return object;
    }
}
