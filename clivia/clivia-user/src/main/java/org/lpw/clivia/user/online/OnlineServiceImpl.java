package org.lpw.clivia.user.online;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.dao.jdbc.SqlTable;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service(OnlineModel.NAME + ".service")
public class OnlineServiceImpl implements OnlineService, SecondsJob, MinuteJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private Header header;
    @Inject
    private Session session;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;
    @Inject
    private OnlineDao onlineDao;
    @Value("${" + OnlineModel.NAME + ".effective:30}")
    private int effective;
    private final Map<String, Long> visits = new ConcurrentHashMap<>();

    @Override
    public JSONObject query(String uid, String ip) {
        return onlineDao.query(authService.users(uid), ip, pagination.getPageSize(20), pagination.getPageNum())
                .toJson((online, object) -> object.put("user", userService.get(online.getUser())));
    }

    @Override
    public OnlineModel findBySid(String sid) {
        return onlineDao.findBySid(sid);
    }

    @Override
    public void signIn(UserModel user) {
        String sid = session.getId();
        OnlineModel online = onlineDao.findBySid(sid);
        if (online == null) {
            online = new OnlineModel();
            online.setSid(sid);
        }
        online.setUser(user.getId());
        online.setGrade(user.getGrade());
        online.setIp(header.getIp());
        online.setSignIn(dateTime.now());
        online.setLastVisit(dateTime.now());
        onlineDao.save(online);
    }

    @Override
    public void visit() {
        visits.put(session.getId(), System.currentTimeMillis());
    }

    @Override
    public void signOut() {
        signOut(onlineDao.findBySid(session.getId()));
    }

    @Override
    public void signOutId(String id) {
        signOut(onlineDao.findById(id));
    }

    @Override
    public void signOutSid(String sid) {
        signOut(onlineDao.findBySid(sid));
    }

    @Override
    public void signOutUser(String user) {
        onlineDao.query(user).getList().forEach(this::signOut);
    }

    @Override
    public int count(Date date, int grade) {
        return count(onlineDao.user(dateTime.toTimeRange(date), grade));
    }

    @Override
    public int count(Timestamp lastVisit, int grade) {
        return count(onlineDao.user(lastVisit, grade));
    }

    private int count(SqlTable sqlTable) {
        Set<String> set = new HashSet<>();
        sqlTable.forEach(list -> set.add((String) list.get(0)));

        return set.size();
    }

    @Override
    public void executeSecondsJob() {
        Map<String, Long> map = new HashMap<>(visits);
        visits.clear();
        map.forEach((sid, time) -> onlineDao.lastVisit(sid, new Timestamp(time)));
    }

    @Override
    public void executeMinuteJob() {
        onlineDao.query(new Timestamp(System.currentTimeMillis() - TimeUnit.Minute.getTime(effective))).getList().forEach(online -> {
            userService.signOut(online.getSid());
            signOut(online);
        });
    }

    private void signOut(OnlineModel online) {
        if (online == null)
            return;

        onlineDao.delete(online);
        visits.remove(online.getSid());
    }
}
