package com.desert.eagle.player.withdraw;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.OverdueListener;
import com.desert.eagle.player.PlayerModel;
import com.desert.eagle.player.PlayerService;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.sql.Date;
import java.util.Calendar;

@Service(WithdrawModel.NAME + ".service")
public class WithdrawServiceImpl implements WithdrawService, OverdueListener {
    @Inject
    private DateTime dateTime;
    @Inject
    private Message message;
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private PlayerService playerService;
    @Inject
    private WithdrawListener listener;
    @Inject
    private WithdrawDao withdrawDao;

    @Override
    public JSONObject query(String uid, int type, int status, String time, int timeN) {
        String player = uid;
        if (!validator.isEmpty(uid)) {
            PlayerModel p = playerService.find(uid);
            if (p != null)
                player = p.getId();
        }
        if (timeN > -1) {
            if (timeN == 0) {
                Date today = dateTime.today();
                time = dateTime.toString(dateTime.getStart(today)) + "," + dateTime.toString(dateTime.getEnd(today));
            } else if (timeN == 1) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                Date date = new Date(calendar.getTimeInMillis());
                time = dateTime.toString(dateTime.getStart(date)) + "," + dateTime.toString(dateTime.getEnd(date));
            } else if (timeN == 2) {
                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                Date start = new Date(calendar.getTimeInMillis());
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                Date end = new Date(calendar.getTimeInMillis());
                time = dateTime.toString(dateTime.getStart(start)) + "," + dateTime.toString(dateTime.getEnd(end));
            }
        }


        return withdrawDao.query(player, type, status, time, pagination.getPageSize(20), pagination.getPageNum())
                .toJson((withdraw, object) -> {
                    object.put("player", playerService.get(withdraw.getPlayer()));
                    object.put("uid", object.getJSONObject("player").getString("uid"));
                    if (validator.isId(withdraw.getAuditor()))
                        object.put("auditor", playerService.get(withdraw.getAuditor()));
                });
    }

    @Override
    public JSONObject user() {
        return withdrawDao.query(userService.id(), pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public int surplus() {
        return surplus(userService.id(), dateTime.now());
    }

    private int surplus(String player, Timestamp now) {
        return keyvalueService.valueAsInt("setting.withdraw.count", 5) - withdrawDao.count(player, dateTime.getStart(now), dateTime.getEnd(now));
    }

    @Override
    public int submit(int type, int amount) {
        if (amount < keyvalueService.valueAsInt("setting.withdraw.min", 50))
            return 1;

        String player = userService.id();
        if (withdrawDao.find(player, 0) != null)
            return 4;

        Timestamp now = dateTime.now();
        if (surplus(player, now) <= 0)
            return 2;

        if (!playerService.withdraw(player, amount * 100, ""))
            return 3;

        WithdrawModel withdraw = new WithdrawModel();
        withdraw.setPlayer(player);
        withdraw.setType(type);
        withdraw.setAmount(amount);
        withdraw.setSubmit(now);
        withdraw.setAuditor("");
        withdrawDao.save(withdraw);

        return 0;
    }

    @Override
    public void pass(String id) {
        WithdrawModel withdraw = withdrawDao.findById(id);
        if (withdraw == null || withdraw.getStatus() != 0)
            return;

        withdraw.setStatus(1);
        withdraw.setCheck(1);
        withdraw.setAudit(dateTime.now());
        withdraw.setAuditor(userService.id());
        withdrawDao.save(withdraw);
        listener.withdraw();
    }

    @Override
    public void reject(String id) {
        WithdrawModel withdraw = withdrawDao.findById(id);
        if (withdraw == null || withdraw.getStatus() != 0)
            return;

        withdraw.setStatus(2);
        withdraw.setCheck(1);
        withdraw.setAudit(dateTime.now());
        withdraw.setAuditor(userService.id());
        withdrawDao.save(withdraw);
        playerService.collect(withdraw.getPlayer(), withdraw.getAmount() * 100, "下分拒绝", "管理员." + userService.fromSession().getNick() + ".拒绝下分请求");
    }

    @Override
    public JSONObject newer() {
        JSONObject object = new JSONObject();
        int count = withdrawDao.count(0);
        object.put("count", count);
        if (count > 0)
            object.put("ringtone", "../audio/xf.mp3");

        return object;
    }

    @Override
    public boolean save(PlayerModel player, int amount) {
        if (!playerService.withdraw(player.getId(), amount * 100, "管理员." + userService.fromSession().getNick() + ".手动下分"))
            return false;

        WithdrawModel withdraw = new WithdrawModel();
        withdraw.setPlayer(player.getId());
        withdraw.setType(3);
        withdraw.setAmount(amount);
        withdraw.setStatus(1);
        withdraw.setCheck(1);
        withdraw.setSubmit(dateTime.now());
        withdraw.setAudit(dateTime.now());
        withdraw.setAuditor(userService.id());
        withdrawDao.save(withdraw);
        listener.withdraw();

        return true;
    }

    @Override
    public int sum(Timestamp start, Timestamp end) {
        return withdrawDao.sum(start, end, 1);
    }

    @Override
    public int sum(String player, Timestamp start, Timestamp end) {
        return withdrawDao.sum(player, start, end, 1);
    }

    @Override
    public void overdue(Timestamp time) {
        withdrawDao.delete(time);
    }
}
