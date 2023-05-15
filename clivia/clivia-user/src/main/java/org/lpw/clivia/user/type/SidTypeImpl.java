package org.lpw.clivia.user.type;

import org.lpw.clivia.increment.IncrementService;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Numeric;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Set;

@Service("clivia.user.type.sid")
public class SidTypeImpl extends TypeSupport {
    @Inject
    private Message message;
    @Inject
    private Numeric numeric;
    @Inject
    private Session session;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private IncrementService incrementService;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;

    @Override
    public String getKey() {
        return Types.Sid;
    }

    @Override
    public UserModel auth(String uid, String password, String grade) {
        UserModel user = userService.fromSession();
        if (user != null)
            return userService.findById(user.getId());

        if (keyvalueService.valueAsInt("setting.user.sign-up.sid", 0) != 1)
            return null;

        uid = session.getId();
        AuthModel auth = authService.findByUid(uid);

        return auth == null ? userService.signUp(uid, password, getKey(), null, grade, getInvitecode())
                : userService.findById(auth.getUser());
    }

    @Override
    public Set<String> getUid(String uid, String password) {
        return Set.of(session.getId());
    }

    @Override
    public String getNick(String uid, String password) {
        return message.get("clivia.user.sid.nick",
                numeric.toString(incrementService.get("clivia.user.sid.nick"), "000"));
    }
}
