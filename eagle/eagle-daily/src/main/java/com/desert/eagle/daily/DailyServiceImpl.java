package com.desert.eagle.daily;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.OverdueListener;
import com.desert.eagle.player.PlayerService;
import com.desert.eagle.player.deposit.DepositListener;
import com.desert.eagle.player.deposit.DepositService;
import com.desert.eagle.player.profit.ProfitService;
import com.desert.eagle.player.withdraw.WithdrawListener;
import com.desert.eagle.player.withdraw.WithdrawService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.online.OnlineService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Service(DailyModel.NAME + ".service")
public class DailyServiceImpl implements DailyService, MinuteJob, OverdueListener, DepositListener, WithdrawListener {
    @Inject
    private Numeric numeric;
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private ProfitService profitService;
    @Inject
    private OnlineService onlineService;
    @Inject
    private PlayerService playerService;
    @Inject
    private DepositService depositService;
    @Inject
    private WithdrawService withdrawService;
    @Inject
    private DailyDao dailyDao;
    private final String allGame = "all";

    @Override
    public JSONObject query(String game, String date) {
        return dailyDao.query(validator.isId(game) ? game : allGame, date, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject today(boolean shortcut) {
        JSONObject object = new JSONObject();
        DailyModel daily = dailyDao.find(dateTime.today(), allGame);
        if (daily == null)
            daily = new DailyModel();
        else {
            object.put("player", daily.getPlayer());
            object.put("count", daily.getCount());
            fill(daily, object);
        }
        if (shortcut) {
            object.put("shortcut", "在线玩家：" + onlineService.count(new Timestamp(System.currentTimeMillis() - TimeUnit.Minute.getTime(3)), 0)
                    + "；玩家余额：" + playerService.balance() / 100
                    + "；下注额：" + daily.getBet() / 100 + "；站点盈利：" + daily.getGain() / 100);
        }

        return object;
    }

    @Override
    public JSONArray day7() {
        Calendar calendar = Calendar.getInstance();
        Date end = new Date(calendar.getTimeInMillis());
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        Date start = new Date(calendar.getTimeInMillis());

        return modelHelper.toJson(dailyDao.query(allGame, start, end).getList(), this::fill);
    }

    private void fill(DailyModel daily, JSONObject object) {
        object.put("date", dateTime.toString(daily.getDate(), "MM-dd"));
        object.put("bet", daily.getBet() / 100);
        object.put("profit", daily.getProfit() / 100);
        object.put("water", daily.getWater() / 100);
        object.put("water0", daily.getWater0() / 100);
        object.put("commission", daily.getCommission() / 100);
        object.put("gain", daily.getGain() / 100);
        object.put("deposit", daily.getDeposit() / 100);
        object.put("withdraw", daily.getWithdraw());
    }

    @Override
    public void executeMinuteJob() {
        Calendar calendar = Calendar.getInstance();
        stat(new Date(calendar.getTimeInMillis()), true);
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            calendar.add(Calendar.HOUR_OF_DAY, -2);
            stat(new Date(calendar.getTimeInMillis()), false);
        }
    }

    private void stat(Date date, boolean today) {
        Map<String, DailyModel> map = new HashMap<>();
        Set<String> set = new HashSet<>();
        profitService.list(date).forEach(profit -> {
            DailyModel daily = map.computeIfAbsent(profit.getGame(), key -> findOrNew(date, profit.getGame()));
            if (profit.getAmount() > 0)
                set.add(profit.getPlayer());
            daily.setPlayer(daily.getPlayer() + 1);
            daily.setCount(daily.getCount() + profit.getCount());
            daily.setBet(daily.getBet() + profit.getAmount());
            daily.setWater(daily.getWater() + profit.getWater());
            daily.setWater0(daily.getWater0() + profit.getWater0());
            daily.setProfit(daily.getProfit() + profit.getAmount() + profit.getProfit());
            daily.setCommission(daily.getCommission() + profit.getCommission());
        });

        Timestamp start = dateTime.getStart(date);
        Timestamp end = dateTime.getEnd(date);
        int register = playerService.register(start, end);
        int deposit = depositService.sum(false, start, end);
        int gift = depositService.sum(true, start, end);
        int withdraw = withdrawService.sum(start, end);
        map.values().forEach(daily -> {
            daily.setGain(daily.getBet() - daily.getProfit());
            if (daily.getGame().equals(allGame)) {
                daily.setRegister(register);
                daily.setDeposit(deposit);
                daily.setGift(gift);
                daily.setWithdraw(withdraw);
                daily.setPlayer(set.size());
            }
            dailyDao.save(daily);
        });
    }

    private DailyModel findOrNew(Date date, String game) {
        DailyModel daily = dailyDao.find(date, game);
        if (daily == null) {
            daily = new DailyModel();
            daily.setDate(date);
            daily.setGame(game);
        } else {
            daily.setPlayer(0);
            daily.setCount(0);
            daily.setBet(0);
            daily.setProfit(0);
            daily.setWater(0);
            daily.setWater0(0);
            daily.setCommission(0);
        }

        return daily;
    }

    @Override
    public void overdue(Timestamp time) {
        dailyDao.delete(new Date(time.getTime()));
    }

    @Override
    public void deposit() {
        Date date = dateTime.today();
        Timestamp start = dateTime.getStart(date);
        Timestamp end = dateTime.getEnd(date);
        DailyModel daily = findOrNew(date, allGame);
        daily.setDeposit(depositService.sum(false, start, end));
        daily.setGift(depositService.sum(true, start, end));
        dailyDao.save(daily);
    }

    @Override
    public void withdraw() {
        Date date = dateTime.today();
        Timestamp start = dateTime.getStart(date);
        Timestamp end = dateTime.getEnd(date);
        DailyModel daily = findOrNew(date, allGame);
        daily.setWithdraw(withdrawService.sum(start, end));
        dailyDao.save(daily);
    }
}
