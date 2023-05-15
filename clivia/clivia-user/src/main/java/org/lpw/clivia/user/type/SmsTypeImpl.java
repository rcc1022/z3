package org.lpw.clivia.user.type;

import org.lpw.clivia.push.PushService;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Set;

@Service("clivia.user.type.sms")
public class SmsTypeImpl extends TypeSupport {
    @Inject
    private PushService pushService;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;

    @Override
    public String getKey() {
        return Types.Sms;
    }

    @Override
    public UserModel auth(String uid, String password, String grade) {
        if (!pushService.captcha(password))
            return null;

        AuthModel auth = authService.findByUid(uid);

        return auth == null ? userService.signUp(uid, password, getKey(), null, grade, getInvitecode()) : userService.findById(auth.getUser());
    }

    @Override
    public Set<String> getUid(String uid, String password) {
        return Set.of(uid);
    }

    @Override
    public String getMobile(String uid, String password) {
        return uid;
    }
}
