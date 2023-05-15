package org.lpw.clivia.weixin;

import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.clivia.user.type.TypeSupport;
import org.lpw.clivia.weixin.info.InfoModel;
import org.lpw.clivia.weixin.info.InfoService;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Validator;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

public abstract class UserTypeSupport extends TypeSupport {
    @Inject
    protected Context context;
    @Inject
    protected Validator validator;
    @Inject
    protected UserService userService;
    @Inject
    protected AuthService authService;
    @Inject
    protected WeixinService weixinService;
    @Inject
    protected InfoService infoService;

    @Override
    public UserModel auth(String uid, String password, String grade) {
        Set<String> set = getUid(uid, password);
        if (set == null)
            return null;

        UserModel user = null;
        Set<String> newer = new HashSet<>();
        String avatar = getAvatar(uid, password);
        for (String u : set) {
            AuthModel auth = authService.findByUid(u);
            if (auth == null)
                newer.add(u);
            else {
                user = userService.findById(auth.getUser());
                authService.update(auth, getKey(), getMobile(uid, password), getEmail(uid, password), getNick(uid, password), avatar);
            }
        }

//        if (user == null) {
//            AuthModel auth = authService.findByAvatar(avatar);
//            if (auth != null)
//                user = userService.findById(auth.getUser());
//        }

        if (user == null)
            user = userService.signUp(uid, password, getKey(), null, grade, getInvitecode());
        else if (!newer.isEmpty())
            for (String u : newer)
                authService.create(user.getId(), u, getKey(), getMobile(uid, password), getEmail(uid, password), getNick(uid, password), getAvatar(uid, password));

        return user;
    }

    @Override
    public Set<String> getUid(String uid, String password) {
        String openId = get(uid, password, "openid");
        if (openId == null)
            return null;

        Set<String> set = new HashSet<>();
        set.add(openId);
        InfoModel info = infoService.find(openId);
        if (!validator.isEmpty(info.getUnionId())) {
            set.add(info.getUnionId());
            infoService.query(info.getUnionId()).forEach(i -> set.add(i.getOpenId()));
        }

        String fingerprint = request.get("fingerprint");
        if (!validator.isEmpty(fingerprint))
            set.add(fingerprint);

        return set;
    }

    protected String key(String uid, String password) {
        return WeixinModel.NAME + ".uid-password." + uid + "-" + password;
    }
}
