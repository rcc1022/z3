package com.desert.eagle.player.profit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.GameModel;
import com.desert.eagle.game.GameService;
import com.desert.eagle.game.OverdueListener;
import com.desert.eagle.player.PlayerModel;
import com.desert.eagle.player.PlayerService;
import com.desert.eagle.player.unwater.UnwaterService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;

@Service(ProfitModel.NAME + ".service")
public class ProfitServiceImpl implements ProfitService, OverdueListener, MinuteJob {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private GameService gameService;
    @Inject
    private PlayerService playerService;
    @Inject
    private UnwaterService unwaterService;
    @Inject
    private ProfitDao profitDao;
    private final String allGame = "all";

    @Override
    public JSONObject query(String uid, String nick, String invitor, String date) {
        Set<String> player = new HashSet<>();
        if (!validator.isEmpty(uid)) {
            PlayerModel model = playerService.find(uid);
            if (model == null)
                return new JSONObject();

            player.add(model.getId());
        } else {
            if (!validator.isEmpty(nick)) {
                player.addAll(userService.ids(null, null, null, nick, null, null, null, null, null));
                if (player.isEmpty())
                    return new JSONObject();
            }

            if (!validator.isEmpty(invitor)) {
                Set<String> set = playerService.subids(invitor);
                if (set.isEmpty())
                    return new JSONObject();

                if (player.isEmpty())
                    player.addAll(set);
                else
                    player.retainAll(set);
            }
        }

        return profitDao.query(player, allGame, date, pagination.getPageSize(20), pagination.getPageNum()).toJson((profit, object) -> {
            PlayerModel p = playerService.findById(profit.getPlayer());
            if (p == null) {
                object.put("player", "");
                object.put("uid", "");
//                object.put("inivtor", "");
            } else {
                object.put("player", playerService.get(p.getId()));
                object.put("uid", p.getUid());
//                PlayerModel i = playerService.findById(p.getInvitor());
//                object.put("invitor", i == null ? "" : i.getUid());
            }
        });
    }

    @Override
    public JSONObject today() {
        return profitDao.queryAllGame(allGame, dateTime.today(), pagination.getPageSize(20), pagination.getPageSize()).toJson((profit, object) -> {
            object.put("player", playerService.get(profit.getPlayer()));
            object.put("uid", object.getJSONObject("player").getString("uid"));
            object.put("balance", playerService.balance(profit.getPlayer()));
            object.put("win", profit.getAmount() + profit.getProfit());
        });
    }

    @Override
    public List<ProfitModel> list(Date date) {
        return profitDao.query(date).getList();
    }

    @Override
    public List<ProfitModel> allGame(Date date) {
        return profitDao.queryAllGame(allGame, date, 0, 0).getList();
    }

    @Override
    public JSONObject user() {
        return profitDao.query(userService.id(), allGame, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject junior(String player) {
        PlayerModel pm = playerService.findById(player);
        if (pm == null || !userService.id().equals(pm.getInvitor())) {
            return new JSONObject();
        }

        return profitDao.query(player, allGame, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public ProfitModel find(String player, Date date) {
        return profitDao.find(player, allGame, date);
    }

    @Override
    public void save(String game, String player, Date date, int count, int amount, int profit, int commission) {
        ProfitModel model = findOrNew(player, game, date);
        model.setCount(count);
        model.setAmount(amount);
        model.setProfit(profit - amount);
        model.setCommission(commission);
        profitDao.save(model);

        ProfitModel all = new ProfitModel();
        all.setPlayer(player);
        all.setGame(allGame);
        all.setDate(date);
        for (ProfitModel m : profitDao.query(player, date).getList()) {
            if (m.getGame().equals(allGame)) {
                all.setId(m.getId());
                all.setDeposit(m.getDeposit());
                all.setWithdraw(m.getWithdraw());
                all.setWater(m.getWater());
                all.setWater2(m.getWater2());
                all.setStatus(m.getStatus());

                continue;
            }

            all.setCount(all.getCount() + m.getCount());
            all.setAmount(all.getAmount() + m.getAmount());
            all.setProfit(all.getProfit() + m.getProfit());
            all.setCommission(all.getCommission() + m.getCommission());
        }
        profitDao.save(all);
        if (dateTime.toString(date).equals(dateTime.toString(dateTime.today())))
            playerService.setBetProfit(player, all.getAmount(), all.getProfit());
    }

    @Override
    public void water(Date date, Map<String, Integer> map) {
        map.forEach((player, water) -> {
            ProfitModel profit = findOrNew(player, allGame, date);
            profit.setWater0(Math.max(0, water - profit.getWater()));
            profitDao.save(profit);
        });
    }

    @Override
    public Map<String, int[]> water(Date start, Date end) {
        return profitDao.water(allGame, start, end);
    }

    @Override
    public void deposit(String player, int amount) {
        ProfitModel profit = findOrNew(player, allGame, dateTime.today());
        profit.setDeposit(profit.getDeposit() + amount);
        profitDao.save(profit);
    }

    @Override
    public void withdraw(String player, int amount) {
        ProfitModel profit = findOrNew(player, allGame, dateTime.today());
        profit.setWithdraw(profit.getWithdraw() + amount);
        profitDao.save(profit);
    }

    private ProfitModel findOrNew(String player, String game, Date date) {
        ProfitModel model = profitDao.find(player, game, date);
        if (model == null) {
            model = new ProfitModel();
            model.setPlayer(player);
            model.setGame(game);
            model.setDate(date);
        }

        return model;
    }

    @Override
    public JSONArray towater() {
        return modelHelper.toJson(profitDao.water(allGame).getList(), (profit, object) -> {
            object.put("player", playerService.get(profit.getPlayer()));
            object.put("uid", object.getJSONObject("player").getString("uid"));
            object.put("balance", playerService.balance(profit.getPlayer()));
            object.put("win", profit.getAmount() + profit.getProfit());
        });
    }

    @Override
    public void passAll() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        profitDao.water(allGame).getList().forEach(profit -> {
            int amount = profit.getWater0();
            profit.setWater(profit.getWater() + amount);
            profit.setWater0(0);
            profitDao.save(profit);
            playerService.collect(profit.getPlayer(), amount, "返水", dateTime.toString(profit.getDate(), "yyyyMMdd"));
        });
    }

    @Override
    public void pass(String id) {
        ProfitModel profit = profitDao.findById(id);
        if (profit == null || profit.getWater0() <= 0)
            return;

        int amount = profit.getWater0();
        profit.setWater(profit.getWater() + amount);
        profit.setWater0(0);
        profitDao.save(profit);
        playerService.collect(profit.getPlayer(), amount, "返水", dateTime.toString(profit.getDate(), "yyyyMMdd"));
    }

    @Override
    public void reject(String id) {
        ProfitModel profit = profitDao.findById(id);
        if (profit == null || profit.getWater0() <= 0)
            return;

        unwaterService.save(profit.getPlayer(), profit.getWater0());
        profit.setWater2(profit.getWater2() + profit.getWater0());
        profit.setWater0(0);
        profitDao.save(profit);
    }

    @Override
    public void overdue(Timestamp time) {
        profitDao.delete(new Date(time.getTime()));
    }

    @Override
    public void executeMinuteJob() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.HOUR_OF_DAY) > 0)
            return;

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date date = new Date(calendar.getTimeInMillis());
        String string = dateTime.toString(date);
        Map<String, GameModel> games = new HashMap<>();
        profitDao.query(date).getList().forEach(profit -> {
            if (profit.getGame().equals(allGame) || profit.getProfit() >= 0 || profit.getWaterLose() > 0)
                return;

            GameModel game = games.computeIfAbsent(profit.getGame(), key -> gameService.get(key));
            if (game == null || game.getLose() <= 0)
                return;

            profit.setWaterLose(profit.getProfit() * game.getLose() / -10000);
            if (profit.getWaterLose() <= 0)
                return;

            profitDao.save(profit);
            playerService.collect(profit.getPlayer(), profit.getWaterLose(), "返水", game.getName() + "/" + string + "/亏损返水");
        });
    }
}
