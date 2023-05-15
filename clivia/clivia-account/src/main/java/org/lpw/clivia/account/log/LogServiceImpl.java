package org.lpw.clivia.account.log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.account.AccountModel;
import org.lpw.clivia.account.AccountService;
import org.lpw.clivia.lock.LockHelper;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.Set;

@Service(LogModel.NAME + ".service")
public class LogServiceImpl implements LogService, UserListener, SecondsJob {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Json json;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private LockHelper lockHelper;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;
    @Inject
    private AccountService accountService;
    @Inject
    private LogDao logDao;
    private final Set<String> ignores = Set.of("user", "account", "owner", "type", "channel", "amount", "balance", "state", "restate", "start", "end");

    @Override
    public JSONObject query(String uid, String owner, String type, String channel, int state, String start) {
        return logDao.query(authService.findUser(uid, uid), owner, type, channel, state, start,
                pagination.getPageSize(20), pagination.getPageNum()).toJson(this::toJson);
    }

    private JSONObject toJson(LogModel log) {
        JSONObject object = modelHelper.toJson(log);
        object.put("user", userService.get(log.getUser()));
        if (!validator.isEmpty(log.getJson())) {
            JSONObject obj = json.toObject(log.getJson());
            if (obj != null) {
                obj.forEach((key, value) -> {
                    if (ignores.contains(key))
                        return;

                    object.put(key, value);
                });
            }
        }

        return object;
    }

    @Override
    public String create(AccountModel account, String type, String channel, long amount, State state, Map<String, String> map) {
        LogModel log = new LogModel();
        log.setUser(account.getUser());
        log.setAccount(account.getId());
        log.setOwner(account.getOwner());
        log.setType(type);
        log.setChannel(validator.isEmpty(channel) ? "" : channel);
        log.setAmount(amount);
        log.setBalance(account.getBalance());
        log.setState(state.ordinal());
        log.setStart(dateTime.now());
        if (!validator.isEmpty(map)) {
            JSONObject json = new JSONObject();
            json.putAll(map);
            log.setJson(json.toJSONString());
        }
        logDao.save(log);

        return log.getId();
    }

    @Override
    public JSONArray pass(String[] ids) {
        JSONArray array = new JSONArray();
        for (String id : ids)
            complete(id, State.Pass, array);

        return array;
    }

    @Override
    public JSONArray reject(String[] ids) {
        JSONArray array = new JSONArray();
        for (String id : ids)
            complete(id, State.Reject, array);

        return array;
    }

    private void complete(String id, State state, JSONArray array) {
        String lockId = lockHelper.lock(LogModel.NAME + ".service.complete:" + id, 1L, 0);
        if (lockId == null)
            return;

        LogModel log = logDao.findById(id);
        if (log == null || log.getState() != State.New.ordinal()) {
            lockHelper.unlock(lockId);

            return;
        }

        log.setState(state.ordinal());
        switch (accountService.complete(log)) {
            case Success -> {
                if (log.getRestate() > 0)
                    log.setRestate(0);
                log.setEnd(dateTime.now());
                if (array != null)
                    array.add(id);
            }
            case Locked -> {
                log.setState(State.New.ordinal());
                log.setRestate(state.ordinal());
            }
            case Failure -> {
                lockHelper.unlock(lockId);
                return;
            }
        }
        logDao.save(log);
        lockHelper.unlock(lockId);
    }

    @Override
    public void userDelete(UserModel user) {
        logDao.delete(user.getId());
    }

    @Override
    public void executeSecondsJob() {
        String lockId = lockHelper.lock(LogModel.NAME + ".service.seconds", 1L, 0);
        if (lockId == null)
            return;

        logDao.query(1).getList().forEach(log -> {
            if (log.getState() == 0)
                complete(log.getId(), State.Pass, null);
        });

        logDao.query(2).getList().forEach(log -> {
            if (log.getState() == 0)
                complete(log.getId(), State.Reject, null);
        });

        lockHelper.unlock(lockId);
    }
}
