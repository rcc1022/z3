package org.lpw.clivia.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.auth.AuthModel;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.clivia.user.crosier.CrosierService;
import org.lpw.clivia.user.illegal.IllegalService;
import org.lpw.clivia.user.invitecode.InvitecodeService;
import org.lpw.clivia.user.inviter.InviterService;
import org.lpw.clivia.user.online.OnlineModel;
import org.lpw.clivia.user.online.OnlineService;
import org.lpw.clivia.user.password.PasswordService;
import org.lpw.clivia.user.type.Types;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.ctrl.context.Response;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service(UserModel.NAME + ".service")
public class UserServiceImpl implements UserService, ContextRefreshedListener {
    private static final String CACHE_MODEL = UserModel.NAME + ".service.model:";
    private static final String CACHE_JSON = UserModel.NAME + ".service.json:";
    private static final String SESSION = UserModel.NAME + ".service.session";
    private static final String SESSION_AUTH3 = UserModel.NAME + ".service.session.auth3";
    private static final String SESSION_UID = UserModel.NAME + ".service.session.uid";

    @Inject
    private Cache cache;
    @Inject
    private Digest digest;
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Session session;
    @Inject
    private Response response;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private Types types;
    @Inject
    private AuthService authService;
    @Inject
    private CrosierService crosierService;
    @Inject
    private OnlineService onlineService;
    @Inject
    private InviterService inviterService;
    @Inject
    private InvitecodeService invitecodeService;
    @Inject
    private PasswordService passwordService;
    @Inject
    private IllegalService illegalService;
    @Inject
    private Optional<Set<UserListener>> listeners;
    @Inject
    private UserDao userDao;
    @Value("${" + UserModel.NAME + ".sync:false}")
    private boolean sync;
    @Value("${" + UserModel.NAME + ".full:0}")
    private int full;
    private final int codeLength = 8;

    @Override
    public boolean isCode(String code) {
        return !validator.isEmpty(code) && code.length() == codeLength;
    }

    @Override
    public boolean isFull() {
        return full > 0 && userDao.count() >= full;
    }

    @Override
    public void setFull(int full) {
        this.full = full;
    }

    @Override
    public UserModel signUp(String uid, String password, String type, String inviter, String grade, String invitecode) {
        UserModel user = fromSession();
        if (user == null)
            user = new UserModel();
        if (Types.Self.equals(type) && validator.isEmpty(user.getPassword()))
            user.setPassword(password(password));
        if (user.getRegister() == null)
            user.setRegister(dateTime.now());
        setCode(user);
        String mobile = types.getMobile(type, uid, password);
        if (!validator.isEmpty(mobile) && validator.isMobile(mobile)) {
            if (validator.isEmpty(user.getMobile()))
                user.setMobile(mobile);
        } else
            mobile = null;
        String email = types.getEmail(type, uid, password);
        if (!validator.isEmpty(email) && validator.isEmail(email)) {
            if (validator.isEmpty(user.getEmail()))
                user.setEmail(email);
        } else
            email = null;
        String avatar = types.getAvatar(type, uid, password);
        if (validator.isEmpty(user.getAvatar()))
            user.setAvatar(avatar);
        String nick = types.getNick(type, uid, password);
        if (!validator.isEmpty(nick))
            user.setNick(nick);
        if (validator.isEmpty(user.getNick()) && type.equals(Types.Self))
            user.setNick(uid);
        setInviter(user, inviter);
        user.setGrade(crosierService.signUpGrade(grade));
        user.setState(1);
        if (validator.isEmpty(user.getFrom()))
            user.setFrom(types.getFrom(type, uid, password));
        userDao.save(user);
        for (String ruid : types.getUid(type, uid, password))
            if (authService.findByUid(ruid) == null)
                authService.create(user.getId(), ruid, type, mobile, email, nick, avatar);
        invitecodeService.use(user.getId(), invitecode);
        UserModel model = user;
        listeners.ifPresent(set -> set.forEach(listener -> listener.userSignUp(model)));
        clearCache(user);
        signIn(user, uid);

        return user;
    }

    private void setCode(UserModel user) {
        for (int i = 0; i < 1024 && user.getCode() == null; i++) {
            String code = generator.random(codeLength);
            if (userDao.findByCode(code) == null)
                user.setCode(code);
        }
    }

    private void setInviter(UserModel user, String code) {
        if (!validator.isEmpty(user.getInviter()))
            return;

        if (validator.isEmpty(code))
            code = inviterService.get();
        if (!isCode(code))
            return;

        UserModel inviter = userDao.findByCode(code.toLowerCase());
        if (inviter == null)
            return;

        user.setInviter(inviter.getId());
        inviter.setInviteCount(inviter.getInviteCount() + 1);
        userDao.save(inviter);
        clearCache(inviter);
    }

    @Override
    public int signIn(String uid, String password, String type, String grade) {
        UserModel user = types.auth(type, uid, password, grade);
        if (user == null)
            return 1;

        if (user.getState() != 1) {
            illegalService.save(user.getId());

            return 2;
        }

        signIn(user, uid);
        if (!Types.Self.equals(type))
            session.set(SESSION_AUTH3, types.getAuth(type, uid, password));

        return 0;
    }

    @Override
    public boolean signInPassword(String id, String password) {
        UserModel user = userDao.findById(id);
        if (user == null)
            return false;

        if (!user.getPassword().equals(password(password)))
            return false;

        signIn(user, id);

        return true;
    }

    @Override
    public boolean signInGesture(String id, String gesture) {
        UserModel user = userDao.findById(id);
        if (user == null)
            return false;

        if (!passwordService.auth(id, "gesture", gesture) || user.getState() != 1)
            return false;

        signIn(user, id);

        return true;
    }

    private void signIn(UserModel user, String uid) {
        onlineService.signIn(user);
        session.set(SESSION, user);
        session.set(SESSION_UID, uid);
        listeners.ifPresent(set -> set.forEach(listener -> listener.userSignIn(user)));
    }

    @Override
    public JSONObject sign() {
        UserModel user = fromSession();
        if (user == null)
            return new JSONObject();

        JSONObject object = getJson(user.getId(), null);
        JSONObject auth3 = session.get(SESSION_AUTH3);
        if (auth3 != null)
            object.put("auth3", auth3);

        return object;
    }

    @Override
    public String id() {
        UserModel user = fromSession();

        return user == null ? null : user.getId();
    }

    @Override
    public int grade() {
        UserModel user = fromSession();

        return user == null ? 0 : user.getGrade();
    }

    @Override
    public void signOut() {
        listeners.ifPresent(set -> set.forEach(listener -> listener.userSignOut(fromSession())));
        onlineService.signOut();
        session.remove(SESSION);
        session.remove(SESSION_AUTH3);
        session.remove(SESSION_UID);
    }

    @Override
    public void signOut(String sid) {
        listeners.ifPresent(set -> set.forEach(listener -> listener.userSignOut(fromSession())));
        session.remove(sid, SESSION);
        session.remove(sid, SESSION_AUTH3);
        session.remove(sid, SESSION_UID);
    }

    @Override
    public void modify(UserModel user) {
        session.set(SESSION, save(fromSession().getId(), user, false));
    }

    @Override
    public boolean password(String oldPassword, String newPassword) {
        UserModel user = fromSession();
        if (!validator.isEmpty(user.getPassword()) && !user.getPassword().equals(password(oldPassword)))
            return false;

        user.setPassword(password(newPassword));
        save(user);
        session.set(SESSION, user);

        return true;
    }

    @Override
    public void resetPassword(String mobile, String password) {
        UserModel user = userDao.findById(authService.findByUid(mobile).getUser());
        user.setPassword(password(password));
        save(user);
    }

    @Override
    public String password(String password) {
        return digest.md5(UserModel.NAME + digest.sha1(password + UserModel.NAME));
    }

    // clivia.user

    @Override
    public void mobile(String mobile) {
        UserModel user = fromSession();
        if (user == null)
            return;

        user.setMobile(mobile);
        modify(user);
    }

    @Override
    public UserModel fromSession() {
        OnlineModel online = onlineService.findBySid(session.getId());
        if (online == null)
            return null;

        UserModel user = session.get(SESSION);
        if (user == null)
            session.set(SESSION, user = findById(online.getUser()));

        return user;
    }

    @Override
    public String uidFromSession() {
        return session.get(SESSION_UID);
    }

    @Override
    public JSONObject get(String[] ids) {
        JSONObject object = new JSONObject();
        for (String id : ids) {
            JSONObject user = getJson(id, null);
            if (user.isEmpty())
                continue;

            object.put(id, user);
        }

        return object;
    }

    @Override
    public JSONObject get(String id) {
        return getJson(id, null);
    }

    @Override
    public JSONObject getNickAvatar(String id) {
        JSONObject user = getJson(id, null);
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("nick", user.get("nick"));
        object.put("avatar", user.getString("avatar"));

        return object;
    }

    @Override
    public UserModel findById(String id) {
        String cacheKey = CACHE_MODEL + id;
        UserModel user = cache.get(cacheKey);
        if (user == null)
            cache.put(cacheKey, user = userDao.findById(id), false);

        return user;
    }

    @Override
    public UserModel findByCode(String code) {
        String cacheKey = CACHE_MODEL + code;
        UserModel user = cache.get(cacheKey);
        if (user == null)
            cache.put(cacheKey, user = userDao.findByCode(code), false);

        return user;
    }

    @Override
    public JSONObject findByUid(String uid) {
        String cacheKey = CACHE_JSON + uid;
        JSONObject object = cache.get(cacheKey);
        if (object == null) {
            UserModel user = findById(authService.findByUid(uid).getUser());
            cache.put(cacheKey, object = user == null ? new JSONObject() : getJson(user.getId(), user), false);
        }

        return object;
    }

    @Override
    public JSONObject findOrSign(String idUidCode) {
        UserModel user = findById(idUidCode);
        if (user == null) {
            AuthModel auth = authService.findByUid(idUidCode);
            if (auth != null)
                user = findById(auth.getUser());
        }
        if (user == null)
            user = userDao.findByCode(idUidCode);

        return user == null ? sign() : getJson(user.getId(), user);
    }

    @Override
    public JSONObject find(String idUidCode) {
        UserModel user = findById(idUidCode);
        if (user == null) {
            AuthModel auth = authService.findByUid(idUidCode);
            if (auth != null)
                user = findById(auth.getUser());
        }
        if (user == null)
            user = userDao.findByCode(idUidCode);

        return user == null ? new JSONObject() : getJson(user.getId(), user);
    }

    @Override
    public JSONArray fill(JSONArray array, String[] names) {
        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject object = array.getJSONObject(i);
            for (String name : names)
                object.put(name, getJson(object.getString(name), null));
        }

        return array;
    }

    private JSONObject getJson(String id, UserModel user) {
        return cache.computeIfAbsent(CACHE_JSON + id, key -> {
            UserModel model = user;
            if (model == null)
                model = findById(id);
            if (model == null)
                return new JSONObject();

            JSONObject object = modelHelper.toJson(model);
            object.put("auth", authService.query(model.getId()));

            return object;
        }, false);
    }

    @Override
    public JSONObject query(String uid, String idcard, String name, String nick, String mobile, String email,
                            String weixin, String qq, String code, int minGrade, int maxGrade, int state, String register,
                            String from) {
        return userDao.query(authService.users(uid), idcard, name, nick, mobile, email, weixin, qq, code, minGrade,
                maxGrade, state, register, from, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public List<UserModel> list(int grade) {
        return userDao.query(grade).getList();
    }

    @Override
    public Set<String> ids(String uid, String idcard, String name, String nick, String mobile, String email, String weixin, String qq, String code) {
        if (!validator.isEmpty(uid))
            return authService.users(uid);

        if (!validator.isEmpty(code)) {
            UserModel user = userDao.findByCode(code);

            return Set.of(user == null ? code : user.getId());
        }

        if (validator.isEmpty(idcard) && validator.isEmpty(name) && validator.isEmpty(nick) && validator.isEmpty(mobile)
                && validator.isEmpty(email) && validator.isEmpty(weixin) && validator.isEmpty(qq))
            return null;

        Set<String> ids = userDao.ids(idcard, name, nick, mobile, email, weixin, qq);
        if (ids.isEmpty())
            ids.add("");

        return ids;
    }

    @Override
    public PageList<UserModel> inviter(String inviter, int pageSize, int pageNum) {
        return userDao.query(inviter, pageSize, pageNum);
    }

    @Override
    public void update(UserModel user) {
        save(user.getId(), user, true);
    }

    private UserModel save(String id, UserModel user, boolean manage) {
        UserModel model = findById(id);
        model.setIdcard(user.getIdcard());
        model.setName(user.getName());
        model.setMobile(user.getMobile());
        model.setEmail(user.getEmail());
        model.setWeixin(user.getWeixin());
        model.setQq(user.getQq());
        model.setNick(user.getNick());
        model.setAvatar(user.getAvatar());
        model.setSignature(user.getSignature());
        model.setGender(user.getGender());
        model.setBirthday(user.getBirthday());
        if (manage) {
            model.setGrade(user.getGrade());
            model.setState(user.getState());
        }
        save(model);

        return model;
    }

    @Override
    public String resetPassword(String id) {
        String password = generator.random(8);
        UserModel user = findById(id);
        user.setPassword(password(password));
        save(user);

        return password;
    }

    @Override
    public void info(String id, String idcard, String name, String nick, String mobile, String email, String weixin, String qq, String signature, int gender) {
        UserModel user = findById(id);
        if (!validator.isEmpty(idcard))
            user.setIdcard(idcard);
        if (!validator.isEmpty(name))
            user.setName(name);
        if (!validator.isEmpty(mobile))
            user.setMobile(mobile);
        if (!validator.isEmpty(email))
            user.setEmail(email);
        if (!validator.isEmpty(weixin))
            user.setWeixin(weixin);
        if (!validator.isEmpty(qq))
            user.setQq(qq);
        if (!validator.isEmpty(nick))
            user.setNick(nick);
        if (!validator.isEmpty(signature))
            user.setSignature(signature);
        if (gender > 0)
            user.setGender(gender);
        save(user);
    }

    @Override
    public void grade(String id, int grade) {
        UserModel user = findById(id);
        user.setGrade(grade);
        save(user);
    }

    @Override
    public void grade(int grade) {
        String id = id();
        grade(id, grade);
        session.set(SESSION, findById(id));
    }

    @Override
    public void state(String id, int state) {
        UserModel user = findById(id);
        user.setState(state);
        save(user);
        onlineService.signOutUser(id);
    }

    @Override
    public boolean root(UserModel user, String password) {
        if (!user.getCode().equals("99999999") || !validator.isEmpty(user.getPassword()) || userDao.count() > 2)
            return false;

        user.setPassword(password(password));
        save(user);

        return true;
    }

    private void save(UserModel user) {
        userDao.save(user);
        clearCache(user);
    }

    @Override
    public int count() {
        return userDao.count();
    }

    @Override
    public int count(Date date) {
        return userDao.count(dateTime.toTimeRange(date));
    }

    @Override
    public UserModel create(String uid, String password, String idcard, String name, String nick, String mobile,
                            String email, String weixin, String qq, String avatar, int gender, Date birthday, String inviter, int grade,
                            int state) {
        UserModel user = new UserModel();
        user.setPassword(password(password));
        user.setIdcard(idcard);
        user.setName(name);
        user.setNick(validator.isEmpty(nick) ? uid : nick);
        if (validator.isEmpty(mobile)) {
            if (validator.isMobile(uid))
                user.setMobile(uid);
        } else
            user.setMobile(mobile);
        if (validator.isEmpty(email)) {
            if (validator.isEmail(uid))
                user.setEmail(uid);
        } else {
            user.setEmail(email);
        }
        user.setWeixin(weixin);
        user.setQq(qq);
        user.setAvatar(avatar);
        user.setGender(gender);
        user.setBirthday(birthday);
        setCode(user);
        user.setInviter(validator.isEmpty(inviter) ? id() : inviter);
        user.setRegister(dateTime.now());
        user.setGrade(grade);
        user.setState(state);
        userDao.save(user);
        authService.create(user.getId(), uid, Types.Self, mobile, email, nick, avatar);
        listeners.ifPresent(set -> set.forEach(listener -> listener.userSignUp(user)));

        return user;
    }

    @Override
    public void insert(UserModel user) {
        userDao.insert(user);
    }

    @Override
    public void delete(String id) {
        destroy(userDao.findById(id));
    }

    @Override
    public boolean destroy(String password) {
        UserModel user = fromSession();
        if (user.getPassword().equals(password(password))) {
            destroy(user);

            return true;
        }

        return false;
    }

    private void destroy(UserModel user) {
        if (user.getGrade() > 98)
            return;

        userDao.delete(user.getId());
        authService.delete(user.getId());
        onlineService.signOutUser(user.getId());
        clearCache(user);
        listeners.ifPresent(set -> set.forEach(listener -> listener.userDelete(user)));
        response.setHeader("user-destroy", user.getId());
        logger.warn(null, "注销用户[{}]。", modelHelper.toJson(user));
    }

    @Override
    public void clearCache() {
        clearCache(fromSession());
    }

    @Override
    public void clearCache(String id) {
        UserModel user = findById(id);
        if (user != null)
            clearCache(user);
    }

    private void clearCache(UserModel user) {
        cache.remove(CACHE_MODEL + user.getId());
        cache.remove(CACHE_MODEL + user.getCode());
        cache.remove(CACHE_JSON + user.getId());
        cache.remove(CACHE_JSON + user.getCode());
    }

    @Override
    public int getContextRefreshedSort() {
        return 151;
    }

    @Override
    public void onContextRefreshed() {
        if (!sync || listeners.isEmpty())
            return;

        Set<UserListener> set = listeners.get();
        userDao.query().getList().forEach(user -> set.forEach(listener -> listener.userSync(user)));
        userDao.close();
    }
}
