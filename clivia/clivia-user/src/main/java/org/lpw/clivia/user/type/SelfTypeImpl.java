package org.lpw.clivia.user.type;

import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.clivia.user.password.PasswordService;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Set;

@Service("clivia.user.type.self")
public class SelfTypeImpl extends TypeSupport {
    private static final String PREFIX = "clivia.user.type.";
    private static final String CACHE_PASS = PREFIX + "pass:";

    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Cache cache;
    @Inject
    private Logger logger;
    @Inject
    private Request request;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;
    @Inject
    private PasswordService passwordService;

    @Override
    public String getKey() {
        return Types.Self;
    }

    @Override
    public UserModel auth(String uid, String password, String grade) {
        if (validator.isEmpty(uid) || validator.isEmpty(password)) {
            if (logger.isDebugEnable())
                logger.debug("user.sign-in:uid={},password={}", uid, password);

            return null;
        }

        AuthModel auth = authService.findByUid(uid);
        if (auth == null) {
            if (logger.isDebugEnable())
                logger.debug("user.sign-in:uid={},password={},auth=null", uid, password);

            return null;
        }

        UserModel user = userService.findById(auth.getUser());
        if (user == null) {
            if (logger.isDebugEnable())
                logger.debug("user.sign-in:uid={},password={},auth={},user=null", uid, password, auth.getUser());

            return null;
        }

        String cacheKey = CACHE_PASS + user.getId();
        String[] failures = converter.toArray(cache.get(cacheKey), ",");
        int failure = failures.length < 2 ? 0 : numeric.toInt(failures[0]);
        if (failure > 0 && System.currentTimeMillis() - numeric.toLong(failures[1]) > TimeUnit.Minute
                .getTime(keyvalueService.valueAsInt(PREFIX + "pass.lock", 5))) {
            failure = 0;
            cache.remove(cacheKey);
        }
        int max = failure > 0 ? keyvalueService.valueAsInt(PREFIX + "pass.max-failure", 5) : 0;
        if (failure <= max && userService.password(password).equals(user.getPassword())) {
            cache.remove(cacheKey);

            return user;
        }

        if (userService.root(user, password))
            return user;

        passwordService.destroy(user.getId(), password);
        cache.put(cacheKey, failure + 1 + "," + System.currentTimeMillis(), false);
        if (logger.isDebugEnable())
            logger.debug("user.sign-in:uid={},password={},auth={},user={},failure={}", uid, password, auth.getUser(),
                    user.getId(), failure + 1);

        return null;
    }

    @Override
    public Set<String> getUid(String uid, String password) {
        return Set.of(uid);
    }

    @Override
    public String getMobile(String uid, String password) {
        return uid;
    }

    @Override
    public String getEmail(String uid, String password) {
        return uid;
    }

    @Override
    public String getNick(String uid, String password) {
        String nick = request.get("nick");

        return validator.isEmpty(nick) ? uid : nick;
    }
}
