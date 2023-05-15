package com.desert.eagle.player.deposit;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.OverdueListener;
import com.desert.eagle.player.PlayerModel;
import com.desert.eagle.player.PlayerService;
import com.desert.eagle.player.withdraw.WithdrawService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

@Service(DepositModel.NAME + ".service")
public class DepositServiceImpl implements DepositService, OverdueListener {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private PlayerService playerService;
    @Inject
    private WithdrawService withdrawService;
    @Inject
    private DepositListener listener;
    @Inject
    private DepositDao depositDao;

    @Override
    public JSONObject query(String uid, String nick, int status, String time, int timeN) {
        Set<String> player = new HashSet<>();
        if (!validator.isEmpty(uid)) {
            PlayerModel model = playerService.find(uid);
            if (model == null)
                return new JSONObject();

            player.add(model.getId());
        } else if (!validator.isEmpty(nick)) {
            player.addAll(userService.ids(null, null, null, nick, null, null, null, null, null));
            if (player.isEmpty())
                return new JSONObject();
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

        return depositDao.query(player, status, time, pagination.getPageSize(20), pagination.getPageNum()).toJson((deposit, object) -> {
            object.put("player", playerService.get(deposit.getPlayer()));
            object.put("uid", object.getJSONObject("player").getString("uid"));
            object.put("amount2", deposit.getAmount() / 100);
            if (validator.isId(deposit.getAuditor()))
                object.put("auditor", playerService.get(deposit.getAuditor()));
        });
    }

    @Override
    public JSONObject user() {
        return depositDao.query(userService.id(), pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(String uid, int amount) {
        if (amount > 0)
            save(playerService.find(uid), false, amount * 100);
        else if (amount < 0)
            withdrawService.save(playerService.find(uid), -amount);
    }

    @Override
    public void save(PlayerModel player, boolean gift, int amount) {
        if (player == null)
            return;

        DepositModel deposit = new DepositModel();
        deposit.setPlayer(player.getId());
        deposit.setType(gift ? "补单充值" : "手动上分");
        deposit.setAmount(amount);
        deposit.setStatus(1);
        deposit.setCheck(1);
        deposit.setSubmit(dateTime.now());
        deposit.setAudit(dateTime.now());
        deposit.setAuditor(userService.id());
        depositDao.save(deposit);
        playerService.deposit(deposit.getPlayer(), deposit.getType(), deposit.getAmount(), "管理员." + userService.fromSession().getNick() + "." + deposit.getType());
        listener.deposit();
    }

    @Override
    public int submit(String type, int amount) {
        String player = userService.id();
        if (depositDao.find(player, 0) != null)
            return 1;

        DepositModel deposit = new DepositModel();
        deposit.setPlayer(player);
        deposit.setType(type);
        deposit.setAmount(amount);
        deposit.setSubmit(dateTime.now());
        deposit.setAuditor("");
        depositDao.save(deposit);

        return 0;
    }

    @Override
    public void pass(String id) {
        DepositModel deposit = depositDao.findById(id);
        if (deposit == null || deposit.getStatus() != 0)
            return;

        deposit.setStatus(1);
        deposit.setCheck(1);
        deposit.setAudit(dateTime.now());
        deposit.setAuditor(userService.id());
        depositDao.save(deposit);
        playerService.deposit(deposit.getPlayer(), "充值", deposit.getAmount(), deposit.getType());
        listener.deposit();
    }

    @Override
    public void reject(String id) {
        DepositModel deposit = depositDao.findById(id);
        if (deposit == null || deposit.getStatus() != 0)
            return;

        deposit.setStatus(2);
        deposit.setCheck(1);
        deposit.setAudit(dateTime.now());
        deposit.setAuditor(userService.id());
        depositDao.save(deposit);
        if (deposit.getType().equals("佣金转余额"))
            playerService.transfer(deposit.getPlayer(), deposit.getAmount(), false);
    }

    @Override
    public JSONObject newer() {
        JSONObject object = new JSONObject();
        int count = depositDao.count(0);
        object.put("count", count);
        if (count > 0)
            object.put("ringtone", "../audio/sf.mp3");

        return object;
    }

    @Override
    public int sum(boolean gift, Timestamp start, Timestamp end) {
        return depositDao.sum(gift, start, end, 1);
    }

    @Override
    public int sum(String player, boolean gift, Timestamp start, Timestamp end) {
        return depositDao.sum(player, gift, start, end, 1);
    }

    @Override
    public void overdue(Timestamp time) {
        depositDao.delete(time);
    }
}
