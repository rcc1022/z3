package com.desert.eagle.bet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.desert.eagle.control.ControlService;
import com.desert.eagle.football.FootballListener;
import com.desert.eagle.football.FootballModel;
import com.desert.eagle.football.FootballService;
import com.desert.eagle.game.GameModel;
import com.desert.eagle.game.GameService;
import com.desert.eagle.game.OverdueListener;
import com.desert.eagle.message.MessageService;
import com.desert.eagle.pcnum.PcnumBet;
import com.desert.eagle.pcnum.PcnumModel;
import com.desert.eagle.pcnum.PcnumService;
import com.desert.eagle.pcnum.PcnumrListener;
import com.desert.eagle.player.PlayerModel;
import com.desert.eagle.player.PlayerService;
import com.desert.eagle.player.deposit.DepositService;
import com.desert.eagle.player.profit.ProfitService;
import com.desert.eagle.player.withdraw.WithdrawService;
import com.desert.eagle.rate.RateModel;
import com.desert.eagle.rate.RateService;
import com.desert.eagle.scnum.ScnumListener;
import com.desert.eagle.scnum.ScnumModel;
import com.desert.eagle.scnum.ScnumService;
import com.desert.eagle.wunum.WunumListener;
import com.desert.eagle.wunum.WunumModel;
import com.desert.eagle.wunum.WunumService;
import org.apache.commons.lang3.ObjectUtils;
import org.lpw.clivia.console.ConsoleHelper;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service(BetModel.NAME + ".service")
public class BetServiceImpl implements BetService, PcnumrListener, ScnumListener, WunumListener, FootballListener, OverdueListener, HourJob {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Numeric numeric;
    @Inject
    private Generator generator;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Request request;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private PlayerService playerService;
    @Inject
    private ProfitService profitService;
    @Inject
    private GameService gameService;
    @Inject
    private PcnumService pcnumService;
    @Inject
    private ScnumService scnumService;
    @Inject
    private WunumService wunumService;
    @Inject
    private RateService rateService;
    @Inject
    private FootballService footballService;
    @Inject
    private MessageService messageService;
    @Inject
    private DepositService depositService;
    @Inject
    private WithdrawService withdrawService;
    @Inject
    private ControlService controlService;
    @Inject
    private BetDao betDao;
    private final String[][] pcRobot = {{"双面", "大", "小", "单", "双", "大单", "大双", "小单", "小双"}, {"特码", "10", "11", "12", "13", "14", "15", "16", "17"}};
    private final String[][] scRobot = {{"冠军", "亚军", "第三名", "第四名", "第五名", "第六名", "第七名", "第八名", "第九名", "第十名", "冠亚和"},
            {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "大", "小", "单", "双"},
            {"3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "大", "小", "单", "双"}};
    private final String[][] wuRobot = {{"第一球", "第二球", "第三球", "第四球", "第五球", "总和"},
            {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "大", "小", "单", "双"}, {"大", "小", "单", "双", "龙", "虎", "和"}};
    private final Set<String> dxds = Set.of("大", "小", "单", "双");
    private final Set<String> dxds2 = Set.of("大单", "大双", "小单", "小双");
    private final Map<String, String> replaces = Map.of("de-fen", "得分", "rang-qiu", "让球", "du-ying", "独赢",
            "de-fen-shang", "上半场得分", "rang-qiu-shang", "上半场让球", "du-ying-shang", "上半场独赢",
            "bo-dan", "波胆", "H", "主队", "C", "客队");
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    @Override
    public JSONObject query(String id, String game, String uid, String issue, int win, String time) {
        if (!validator.isEmpty(uid)) {
            PlayerModel player = playerService.find(uid);
            if (player != null)
                id = player.getId();
        }

        JSONObject object = betDao.query(game, id, issue, win, time, pagination.getPageSize(20), pagination.getPageNum()).toJson((bet, obj) -> {
            JSONObject player = playerService.get(bet.getPlayer());
            obj.put("player", player);
            obj.put("uid", player.getString("uid"));
            if (replaces.containsKey(bet.getType()))
                obj.put("type", replaces.get(bet.getType()) + bet.getSubitem());
            if (replaces.containsKey(bet.getItem()))
                obj.put("item", replaces.get(bet.getItem()));
        });

        Map<String, String> map = new HashMap<>();
        map.putAll(pcnumService.nextGameIssue(game));
        map.putAll(scnumService.nextGameIssue(game));
        object.put("summary", "本期投注总额" + (map.isEmpty() ? 0 : (betDao.sumAmount(0, 0, map) / 100)));

        return object;
    }

    @Override
    public String immediately() {
        JSONArray rArray = new JSONArray();
        //查询游戏信息
        gameService.list().stream().filter(x -> x.getOn() == 1).forEach(game -> {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("gameName", game.getName());
            JSONObject latest = new JSONObject();
            if (game.getType() >= 6 && game.getType() <= 9) {
                latest = scnumService.latest(game.getId());
            }
            if (game.getType() < 5) {
                latest = pcnumService.latest(game.getId());
            }
            if (game.getType() >= 10 && game.getType() < 11) {
                latest = wunumService.latest(game.getId());
            }
            long issue = latest.getLongValue("next");
            if (ObjectUtils.isNotEmpty(issue)) {
                jsonObject.put("issue", issue + "期");
                long open = latest.getLongValue("close");
                jsonObject.put("open", open);
                //查询对应游戏下的用户下单记录，倒排
                List<BetModel> bets = betDao.queryUserBetList(game.getId(), issue + "").getList();
                if (!CollectionUtils.isEmpty(bets) && bets.size() != 0) {
                    Map<String, List<BetModel>> userBetMap = bets.stream().collect(Collectors.groupingBy(BetModel::getType));

                    List<UserBetModel> userBetModels = new ArrayList<>();
                    userBetMap.forEach((key, value) -> {
                        UserBetModel userBetModel = new UserBetModel();
                        userBetModel.setType(key);
                        List<UserBetModel.UserBets> userBetsList = value.stream().map(x -> {
                            UserBetModel.UserBets userBets = new UserBetModel.UserBets();
                            userBets.setItem(x.getItem());
                            userBets.setRate(0 != x.getRate() ? new BigDecimal(x.getRate() + "").divide(new BigDecimal("100")).toPlainString() : "");
                            userBets.setAmount(x.getAmount() / 100 + "");
                            return userBets;
                        }).collect(Collectors.toList());
                        userBetModel.setUserBetsList(userBetsList);
                        userBetModels.add(userBetModel);
                    });
                    jsonObject.put("list", JSON.toJSONString(userBetModels));
                }
            }
            rArray.add(jsonObject);
        });
        return rArray.toJSONString();
    }

    public static String secToTime(long seconds) {
        if (0 == seconds) {
            return "0";
        }
        long hour = seconds / 3600;
        long minute = (seconds - hour * 3600) / 60;
        long second = (seconds - hour * 3600 - minute * 60);

        StringBuffer sb = new StringBuffer();
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (second == 0) {
            sb.append("<1秒");
        }
        return sb.toString();
    }


    @Override
    public JSONObject user(String game, String time) {
        return toJson(betDao.query(game, userService.id(), null, -1, time, pagination.getPageSize(20), pagination.getPageNum()));
    }

    private JSONObject toJson(PageList<BetModel> pl) {
        Map<String, JSONObject> games = new HashMap<>();
        Map<String, JSONObject> pcnums = new HashMap<>();
        Map<String, JSONObject> scnums = new HashMap<>();
        Map<String, JSONObject> wunums = new HashMap<>();
        Map<String, JSONObject> footballs = new HashMap<>();
        return pl.toJson((bet, object) -> {
            JSONObject game = games.computeIfAbsent(bet.getGame(), id -> gameService.json(id));
            object.put("game", game);
            int type = game.getIntValue("type");
            if (type <= 5) {
                int t = type / 3;
                object.put("pc", pcnums.computeIfAbsent(t + ":" + bet.getIssue(), key -> pcnumService.get(t, numeric.toLong(bet.getIssue()))));
            } else if (type <= 9) {
                int t = type - 6;
                object.put("sc", scnums.computeIfAbsent(t + ":" + bet.getIssue(), key -> scnumService.get(t, numeric.toLong(bet.getIssue()))));
            } else if (type <= 11) {
                int t = type - 10;
                object.put("wu", wunums.computeIfAbsent(t + ":" + bet.getIssue(), key -> wunumService.get(t, numeric.toLong(bet.getIssue()))));
            } else if (type == 12) {
                String[] issue = bet.getIssue().split(":");
                object.put("football", footballs.computeIfAbsent(bet.getIssue(), key -> footballService.get(numeric.toInt(issue[0]), issue[1])));
            }
        });
    }

    @Override
    public int save(String gid, String issue, JSONArray items) {
        GameModel game = gameService.get(gid);
        if (game == null || validator.isEmpty(items))
            return 1;

        if (game.getType() <= 5)
            return pc(game, issue, items);

        if (game.getType() <= 9)
            return sc(game, issue, items);

        if (game.getType() <= 11)
            return wu(game, issue, items);

        if (game.getType() == 12)
            return football(game, items);

        return 0;
    }

    private int pc(GameModel game, String issue, JSONArray items) {
        if (pcnumService.close(game, numeric.toLong(issue)))
            return 2;

        List<BetModel> list = new ArrayList<>();
        int sum = 0;
        for (int i = 0, size = items.size(); i < size; i++) {
            JSONObject object = items.getJSONObject(i);
            String type = object.getString("type");
            String item = object.getString("name");
            RateModel rate = rateService.find(game.getId(), type, item);
            if (rate == null || rate.getAmount() <= 0)
                return 3;

            int amount = object.getIntValue("amount");
            if (amount < game.getMin())
                return game.getMin() * 10 + 4;

            if (amount > game.getMax() || (rate.getMax() > 0 && amount > rate.getMax()))
                return game.getMax() * 10 + 5;

            BetModel bet = new BetModel();
            bet.setType(object.getString("type"));
            bet.setItem(item);
            bet.setAmount(amount * 100);
            bet.setRate(rate.getAmount());
            list.add(bet);
            sum += bet.getAmount();
        }

        if (betDao.sum(game.getId(), issue, 0, 0) + sum > game.getTotal() * 100)
            return 6;

        String player = userService.id();
        if (playerService.noEnough(player, sum))
            return 7;

        Timestamp time = dateTime.now();
        list.forEach(bet -> {
            bet.setMemo("投注,[" + game.getName() + "]第[" + issue + "]期,投注内容[" + bet.getType() + "/" + bet.getItem() + "],金额[" + bet.getAmount() / 100 + "]元");
            if (!playerService.bet(player, bet.getAmount(), bet.getMemo()))
                return;

            bet.setGame(game.getId());
            bet.setPlayer(player);
            bet.setIssue(issue);
            bet.setWater(bet.getAmount() * game.getWater() / 10000);
            bet.setCommission(bet.getAmount() * game.getCommission() / 10000);
            bet.setTime(time);
            betDao.save(bet);
        });

        JSONObject object = new JSONObject();
        object.put("type", "pc");
        object.put("issue", issue);
        object.put("items", items);
        object.put("sum", sum);
        messageService.save(game.getId(), player, MessageService.bet, object.toJSONString());

        return 0;
    }

    private int sc(GameModel game, String issue, JSONArray items) {
        if (scnumService.close(game, numeric.toLong(issue)))
            return 2;

        List<BetModel> list = new ArrayList<>();
        int sum = 0;
        for (int i = 0, size = items.size(); i < size; i++) {
            JSONObject object = items.getJSONObject(i);
            String type = object.getString("type");
            String item = object.getString("name");
            RateModel rate = rateService.find(game.getId(), type, item);
            if (rate == null || rate.getAmount() <= 0)
                return 3;

            int amount = object.getIntValue("amount");
            if (amount < game.getMin())
                return game.getMin() * 10 + 4;

            if (amount > game.getMax() || (rate.getMax() > 0 && amount > rate.getMax()))
                return game.getMax() * 10 + 5;

            BetModel bet = new BetModel();
            bet.setType(type);
            bet.setItem(item);
            bet.setAmount(amount * 100);
            bet.setRate(rate.getAmount());
            list.add(bet);
            sum += bet.getAmount();
        }

        if (betDao.sum(game.getId(), issue, 0, 0) + sum > game.getTotal() * 100)
            return 6;

        String player = userService.id();
        if (playerService.noEnough(player, sum))
            return 7;

        Timestamp time = dateTime.now();
        list.forEach(bet -> {
            bet.setMemo("投注,[" + game.getName() + "]第[" + issue + "]期,投注内容[" + bet.getType() + "/" + bet.getItem() + "],金额[" + bet.getAmount() / 100 + "]元");
            if (!playerService.bet(player, bet.getAmount(), bet.getMemo()))
                return;

            bet.setGame(game.getId());
            bet.setPlayer(player);
            bet.setIssue(issue);
            bet.setWater(bet.getAmount() * game.getWater() / 10000);
            bet.setCommission(bet.getAmount() * game.getCommission() / 10000);
            bet.setTime(time);
            betDao.save(bet);
        });

        JSONObject object = new JSONObject();
        object.put("type", "sc");
        object.put("issue", issue);
        object.put("items", items);
        object.put("sum", sum);
        messageService.save(game.getId(), player, MessageService.bet, object.toJSONString());

        return 0;
    }

    private int wu(GameModel game, String issue, JSONArray items) {
        if (wunumService.close(game, numeric.toLong(issue)))
            return 2;

        List<BetModel> list = new ArrayList<>();
        int sum = 0;
        for (int i = 0, size = items.size(); i < size; i++) {
            JSONObject object = items.getJSONObject(i);
            String type = object.getString("type");
            String item = object.getString("name");
            RateModel rate = rateService.find(game.getId(), type, item);
            if (rate == null || rate.getAmount() <= 0)
                return 3;

            int amount = object.getIntValue("amount");
            if (amount < game.getMin())
                return game.getMin() * 10 + 4;

            if (amount > game.getMax() || (rate.getMax() > 0 && amount > rate.getMax()))
                return game.getMax() * 10 + 5;

            BetModel bet = new BetModel();
            bet.setType(type);
            bet.setItem(item);
            bet.setAmount(amount * 100);
            bet.setRate(rate.getAmount());
            list.add(bet);
            sum += bet.getAmount();
        }

        if (betDao.sum(game.getId(), issue, 0, 0) + sum > game.getTotal() * 100)
            return 6;

        String player = userService.id();
        if (playerService.noEnough(player, sum))
            return 7;

        Timestamp time = dateTime.now();
        list.forEach(bet -> {
            bet.setMemo("投注,[" + game.getName() + "]第[" + issue + "]期,投注内容[" + bet.getType() + "/" + bet.getItem() + "],金额[" + bet.getAmount() / 100 + "]元");
            if (!playerService.bet(player, bet.getAmount(), bet.getMemo()))
                return;

            bet.setGame(game.getId());
            bet.setPlayer(player);
            bet.setIssue(issue);
            bet.setWater(bet.getAmount() * game.getWater() / 10000);
            bet.setCommission(bet.getAmount() * game.getCommission() / 10000);
            bet.setTime(time);
            betDao.save(bet);
        });

        JSONObject object = new JSONObject();
        object.put("type", "wu");
        object.put("issue", issue);
        object.put("items", items);
        object.put("sum", sum);
        messageService.save(game.getId(), player, MessageService.bet, object.toJSONString());

        return 0;
    }

    private int football(GameModel game, JSONArray items) {
        List<BetModel> list = new ArrayList<>();
        int sum = 0;
        int group = 0;
        String gid = "";
        for (int i = 0, size = items.size(); i < size; i++) {
            JSONObject object = items.getJSONObject(i);
            group = object.getIntValue("group");
            gid = object.getString("gid");
            String type = object.getString("type");
            String item = object.getString("item");
            String rate = footballService.rate(group, gid, type, item);
            if (rate == null)
                return 2;

            long r1 = Math.round(numeric.toDouble(rate) * 100);
            long r2 = Math.round(numeric.toDouble(object.getString("rate")) * 100);
            if (r1 != r2) {
                logger.info("足球下注赔率不匹配：[{}={}:{}={}]", rate, r1, object.getString("rate"), r2);

                return 3;
            }

            int amount = object.getIntValue("amount");
            if (amount < game.getMin())
                return 4;

            if (amount > game.getMax())
                return 5;

            if (!footballService.check(gid, type, item, rate))
                return 3;

            BetModel bet = new BetModel();
            bet.setIssue(group + ":" + gid);
            bet.setType(type);
            bet.setItem(item);
            bet.setSubitem(object.getString("subitem"));
            bet.setAmount(amount * 100);
            bet.setRate((int) r1);
            list.add(bet);
            sum += bet.getAmount();
        }

        if (betDao.sum(game.getId(), gid, 0, 0) + sum > game.getTotal() * 100)
            return 6;

        String player = userService.id();
        if (playerService.noEnough(player, sum))
            return 7;

        Timestamp time = dateTime.now();
        FootballModel football = footballService.find(group, gid);
        list.forEach(bet -> {
            bet.setMemo("投注,[" + game.getName() + "]" + football.getTeamH() + "vs" + football.getTeamC()
                    + ",投注内容[" + bet.getType() + "/" + bet.getItem() + "],金额[" + bet.getAmount() / 100 + "]元");
            if (!playerService.bet(player, bet.getAmount(), bet.getMemo()))
                return;

            bet.setGame(game.getId());
            bet.setPlayer(player);
            bet.setWater(bet.getAmount() * game.getWater() / 10000);
            bet.setCommission(bet.getAmount() * game.getCommission() / 10000);
            bet.setTime(time);
            betDao.save(bet);
        });

        return 0;
    }

    @Override
    public void robot(String gid, String robot) {
        GameModel game = gameService.get(gid);
        if (game == null)
            return;

        BetModel bet = null;
        if (game.getType() <= 5)
            bet = pcRobot(game);
        else if (game.getType() <= 9)
            bet = scRobot(game);
        else if (game.getType() <= 11)
            bet = wuRobot(game);
        if (bet == null)
            return;

        bet.setGame(game.getId());
        bet.setPlayer(robot);
        bet.setAmount(generator.random(keyvalueService.valueAsInt("setting.robot.min", 10), keyvalueService.valueAsInt("setting.robot.max", 400)) * 100);
        bet.setRobot(1);
        bet.setTime(dateTime.now());
        betDao.save(bet);

        JSONObject item = new JSONObject();
        item.put("type", bet.getType());
        item.put("name", bet.getItem());
        item.put("amount", bet.getAmount() / 100);
        JSONArray items = new JSONArray();
        items.add(item);

        JSONObject object = new JSONObject();
        object.put("type", game.getType() <= 5 ? "pc" : "sc");
        object.put("issue", bet.getIssue());
        object.put("items", items);
        object.put("sum", bet.getAmount());
        messageService.save(game.getId(), robot, MessageService.bet, object.toJSONString());
    }

    private BetModel pcRobot(GameModel game) {
        JSONObject latest = pcnumService.latest(game.getId());
        long issue = latest.getLongValue("next");
        if (pcnumService.close(game, issue))
            return null;

        BetModel bet = new BetModel();
        bet.setIssue(numeric.toString(issue));
        String[] array = pcRobot[generator.random(0, pcRobot.length - 1)];
        bet.setType(array[0]);
        bet.setItem(array[generator.random(1, array.length - 1)]);

        return bet;
    }

    private BetModel scRobot(GameModel game) {
        JSONObject latest = scnumService.latest(game.getId());
        long issue = latest.getLongValue("next");
        if (scnumService.close(game, issue))
            return null;

        int last = scRobot[0].length - 1;
        int type = generator.random(0, last);
        int item = type == last ? 2 : 1;

        BetModel bet = new BetModel();
        bet.setIssue(numeric.toString(issue));
        bet.setType(scRobot[0][type]);
        bet.setItem(scRobot[item][generator.random(0, scRobot[item].length - 1)]);

        return bet;
    }

    private BetModel wuRobot(GameModel game) {
        JSONObject latest = wunumService.latest(game.getId());
        long issue = latest.getLongValue("next");
        if (wunumService.close(game, issue))
            return null;

        int last = wuRobot[0].length - 1;
        int type = generator.random(0, last);
        int item = type == last ? 2 : 1;

        BetModel bet = new BetModel();
        bet.setIssue(numeric.toString(issue));
        bet.setType(wuRobot[0][type]);
        bet.setItem(wuRobot[item][generator.random(0, wuRobot[item].length - 1)]);

        return bet;
    }

    @Override
    public int newer(PcnumModel pcnum) {
        int profit = 0;
        Map<String, Integer> commissions = new HashMap<>();
        Map<String, GameModel> games = new HashMap<>();
        String sum = numeric.toString(pcnum.getSum());
        int[] ns = new int[]{pcnum.getNum1(), pcnum.getNum2(), pcnum.getNum3()};
        Arrays.sort(ns);
        boolean shunzi = (ns[1] - ns[0] == 1 && ns[2] - ns[1] == 1) || (ns[0] == 0 && (ns[1] == 1 || ns[1] == 8) && ns[2] == 9);
        Set<Integer> set = new HashSet<>();
        for (int n : ns)
            set.add(n);
        int size = set.size();
        boolean baozi = size == 1;
        boolean duizi = size == 2;
        boolean da = pcnum.getSum() >= 14;
        boolean xiao = pcnum.getSum() <= 13;
        boolean dan = pcnum.getSum() % 2 == 1;
        boolean shuang = pcnum.getSum() % 2 == 0;
        boolean jida = pcnum.getSum() >= 22;
        boolean jixiao = pcnum.getSum() <= 5;
        boolean is13 = pcnum.getSum() == 13;
        boolean is14 = pcnum.getSum() == 14;
        Set<String> zhuihao = new HashSet<>();
        for (BetModel bet : betDao.query(numeric.toString(pcnum.getIssue()), 0, 0).getList()) {
            GameModel game = games.computeIfAbsent(bet.getGame(), id -> gameService.get(id));
            if (game == null || game.getType() / 3 != pcnum.getType())
                continue;

            int rate = 0;
            if (bet.getItem().equals(sum) || (bet.getItem().equals("极大") && jida) || (bet.getItem().equals("极小") && jixiao)
                    || (bet.getItem().equals("顺子") && shunzi) || (bet.getItem().equals("对子") && duizi) || (bet.getItem().equals("豹子") && baozi)) {
                rate = bet.getRate();
            } else if ((bet.getItem().equals("大") && da) || (bet.getItem().equals("小") && xiao)
                    || (bet.getItem().equals("单") && dan) || (bet.getItem().equals("双") && shuang)) {
                if (game.getType() % 3 == 0 && ((is13 && (xiao || dan)) || (is14 && (da || shuang)))) {
                    rate = 160;
                } else if (game.getType() % 3 == 1 && ((is13 && (xiao || dan)) || (is14 && (da || shuang)) || baozi || duizi || shunzi)) {
                    rate = 100;
                } else if (game.getType() % 3 == 2 && ((is13 && (xiao || dan)) || (is14 && (da || shuang)) || ns[0] == 0 || ns[2] == 9)) {
                    rate = 100;
                } else {
                    rate = bet.getRate();
                }
            } else if ((bet.getItem().equals("大单") && da && dan) || (bet.getItem().equals("大双") && da && shuang)
                    || (bet.getItem().equals("小单") && xiao && dan) || (bet.getItem().equals("小双") && xiao && shuang)) {
                if ((is13 && xiao && dan) || (is14 && da && shuang) || (game.getType() % 3 == 1 && (duizi || shunzi || baozi))
                        || (game.getType() % 3 == 2 && (ns[0] == 0 || ns[2] == 9))) {
                    rate = 100;
                } else {
                    rate = bet.getRate();
                }
            }
            if (rate > 0) {
                bet.setProfit(Math.round(0.01f * bet.getAmount() * rate));
                playerService.win(bet.getPlayer(), bet.getProfit(),
                        bet.getMemo().replaceFirst("下注", "开奖") + ",中奖金额[" + bet.getProfit() / 100 + "]元");
            }
            profit += bet.getProfit() - bet.getAmount();
            bet.setOpen(pcnum.getNum1() + "," + pcnum.getNum2() + "," + pcnum.getNum3());
            bet.setStatus(1);
            betDao.save(bet);
            if (bet.getStop() == 1 && bet.getProfit() > 0)
                zhuihao.add(bet.getZhuihao());
            commissions.put(bet.getPlayer(), commissions.getOrDefault(bet.getPlayer(), 0) + bet.getCommission());
        }
        sum();
        stopZhuihao(zhuihao);
        playerService.commission(commissions);

        return profit;
    }

    @Override
    public void close(GameModel game, PcnumModel pcnum) {
        close(game, pcnum.getIssue());
    }

    @Override
    public List<PcnumBet> sumPcAmountProfit(long issue) {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        for (int i = 3; i <= 5; i++) {
            GameModel game = gameService.find(i);
            if (game != null)
                map.put(game.getId(), game.getType() % 3);
        }

        return sumPcAmountProfit(map, issue);
    }

    private List<PcnumBet> sumPcAmountProfit(Map<String, Integer> game, long issue) {
        List<PcnumBet> list = new ArrayList<>();
        long amount = 0;
        for (BetModel bet : betDao.query(numeric.toString(issue), 0, 0).getList()) {
            if (!game.containsKey(bet.getGame()))
                continue;

            amount += bet.getAmount();
            PcnumBet pcnumBet = new PcnumBet();
            pcnumBet.type = game.get(bet.getGame());
            pcnumBet.typeItem = bet.getType() + "/" + bet.getItem();
            pcnumBet.profit = (long) bet.getAmount() * bet.getRate() / 100 - bet.getAmount();
            if (pcnumBet.type == 0)
                pcnumBet.s1314 = (long) bet.getAmount() * 60 / 100;
            list.add(pcnumBet);
        }
        if (amount > 0 && !list.isEmpty()) {
            PcnumBet pb = new PcnumBet();
            pb.profit = amount;
            list.add(pb);
        }

        return list;
    }

    @Override
    public int newer(ScnumModel scnum) {
        int profit = 0;
        Map<String, Integer> commissions = new HashMap<>();
        Map<String, GameModel> games = new HashMap<>();
        String sum = numeric.toString(scnum.getSum());
        Set<String> zhuihao = new HashSet<>();
        for (BetModel bet : betDao.query(numeric.toString(scnum.getIssue()), 0, 0).getList()) {
            GameModel game = games.computeIfAbsent(bet.getGame(), id -> gameService.get(id));
            if (game == null || game.getType() - 6 != scnum.getType())
                continue;

            String type = bet.getType();
            String item = bet.getItem();
            if (validator.isEmpty(type) || validator.isEmpty(item))
                continue;

            int rate = 0;
            if (type.equals("冠亚和")) {
                if (item.equals(sum) || (item.equals("大") && scnum.getSum() >= 12) || (item.equals("小") && scnum.getSum() <= 11)
                        || (item.equals("单") && scnum.getSum() % 2 == 1) || (item.equals("双") && scnum.getSum() % 2 == 0)) {
                    rate = bet.getRate();
                }
            } else {
                int[] num = switch (type) {
                    case "冠军" -> new int[]{scnum.getNum1(), scnum.getNum10()};
                    case "亚军" -> new int[]{scnum.getNum2(), scnum.getNum9()};
                    case "第三名" -> new int[]{scnum.getNum3(), scnum.getNum8()};
                    case "第四名" -> new int[]{scnum.getNum4(), scnum.getNum7()};
                    case "第五名" -> new int[]{scnum.getNum5(), scnum.getNum6()};
                    case "第六名" -> new int[]{scnum.getNum6(), scnum.getNum5()};
                    case "第七名" -> new int[]{scnum.getNum7(), scnum.getNum4()};
                    case "第八名" -> new int[]{scnum.getNum8(), scnum.getNum3()};
                    case "第九名" -> new int[]{scnum.getNum9(), scnum.getNum2()};
                    case "第十名" -> new int[]{scnum.getNum10(), scnum.getNum1()};
                    default -> null;
                };
                if (num == null)
                    continue;

                if (numeric.toString(num[0]).equals(item) || (item.equals("大") && num[0] >= 6) || (item.equals("小") && num[0] <= 5)
                        || (item.equals("单") && num[0] % 2 == 1) || (item.equals("双") && num[0] % 2 == 0)
                        || (item.equals("龙") && num[0] > num[1]) || (item.equals("虎") && num[0] < num[1])) {
                    rate = bet.getRate();
                }
            }
            if (rate > 0) {
                bet.setProfit(Math.round(0.01f * bet.getAmount() * rate));
                playerService.win(bet.getPlayer(), bet.getProfit(),
                        bet.getMemo().replaceFirst("下注", "开奖") + ",中奖金额[" + bet.getProfit() / 100 + "]元");
            }
            profit += bet.getProfit() - bet.getAmount();
            bet.setOpen(scnum.getNum1() + "," + scnum.getNum2() + "," + scnum.getNum3() + "," + scnum.getNum4() + "," + scnum.getNum5() + ","
                    + scnum.getNum6() + "," + scnum.getNum7() + "," + scnum.getNum8() + "," + scnum.getNum9() + "," + scnum.getNum10());
            bet.setStatus(1);
            betDao.save(bet);
            if (bet.getStop() == 1 && bet.getProfit() > 0)
                zhuihao.add(bet.getZhuihao());
            commissions.put(bet.getPlayer(), commissions.getOrDefault(bet.getPlayer(), 0) + bet.getCommission());
        }
        sum();
        stopZhuihao(zhuihao);
        playerService.commission(commissions);

        return profit;
    }

    @Override
    public int newer(WunumModel wunum) {
        int profit = 0;
        Map<String, Integer> commissions = new HashMap<>();
        Map<String, GameModel> games = new HashMap<>();
        Set<String> zhuihao = new HashSet<>();
        for (BetModel bet : betDao.query(numeric.toString(wunum.getIssue()), 0, 0).getList()) {
            GameModel game = games.computeIfAbsent(bet.getGame(), id -> gameService.get(id));
            if (game == null || game.getType() < 10 || game.getType() > 11)
                continue;

            String type = bet.getType();
            String item = bet.getItem();
            if (validator.isEmpty(type) || validator.isEmpty(item))
                continue;

            int rate = 0;
            if (type.equals("总和")) {
                if ((item.equals("大") && wunum.getSum() >= 23) || (item.equals("小") && wunum.getSum() <= 22)
                        || (item.equals("单") && wunum.getSum() % 2 == 1) || (item.equals("双") && wunum.getSum() % 2 == 0)
                        || (item.equals("龙") && wunum.getNum1() > wunum.getNum5()) || (item.equals("虎") && wunum.getNum1() < wunum.getNum5())
                        || (item.equals("和") && wunum.getNum1() == wunum.getNum5())) {
                    rate = bet.getRate();
                }
            } else {
                int num = switch (type) {
                    case "第一球" -> wunum.getNum1();
                    case "第二球" -> wunum.getNum2();
                    case "第三球" -> wunum.getNum3();
                    case "第四球" -> wunum.getNum4();
                    case "第五球" -> wunum.getNum5();
                    default -> -1;
                };
                if (num == -1)
                    continue;

                if (numeric.toString(num).equals(item) || (item.equals("大") && num >= 5) || (item.equals("小") && num <= 4)
                        || (item.equals("单") && num % 2 == 1) || (item.equals("双") && num % 2 == 0)) {
                    rate = bet.getRate();
                }
            }
            if (rate > 0) {
                bet.setProfit(Math.round(0.01f * bet.getAmount() * rate));
                playerService.win(bet.getPlayer(), bet.getProfit(),
                        bet.getMemo().replaceFirst("下注", "开奖") + ",中奖金额[" + bet.getProfit() / 100 + "]元");
            }
            profit += bet.getProfit() - bet.getAmount();
            bet.setOpen(wunum.getNum1() + "," + wunum.getNum2() + "," + wunum.getNum3() + "," + wunum.getNum4() + "," + wunum.getNum5());
            bet.setStatus(1);
            betDao.save(bet);
            if (bet.getStop() == 1 && bet.getProfit() > 0)
                zhuihao.add(bet.getZhuihao());
            commissions.put(bet.getPlayer(), commissions.getOrDefault(bet.getPlayer(), 0) + bet.getCommission());
        }
        sum();
        stopZhuihao(zhuihao);
        playerService.commission(commissions);

        return profit;
    }

    private void sum() {
        singleThreadExecutor.submit(() -> {
            Calendar calendar = Calendar.getInstance();
            sum(new Date(calendar.getTimeInMillis()));
            if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                sum(new Date(calendar.getTimeInMillis()));
            }
            betDao.close();
        });
    }

    private void sum(Date date) {
        Map<String, Integer> map = new HashMap<>();
        betDao.sum(1, 0, dateTime.getStart(date), dateTime.getEnd(date)).forEach(row -> {
            String player = (String) row.get(1);
            profitService.save((String) row.get(0), player, date, numeric.toInt(row.get(2)),
                    numeric.toInt(row.get(3)), numeric.toInt(row.get(5)), numeric.toInt(row.get(6)));
            map.put(player, map.getOrDefault(player, 0) + numeric.toInt(row.get(4)));
        });
        profitService.water(date, map);
    }

    private void stopZhuihao(Set<String> set) {
        set.forEach(zhuihao -> {
            betDao.query(0, zhuihao).getList().forEach(bet -> {
                betDao.delete(bet);
                playerService.unbet(bet.getPlayer(), bet.getAmount(), bet.getMemo() + ",撤单");
            });
        });
    }

    @Override
    public void close(GameModel game, ScnumModel scnum) {
        close(game, scnum.getIssue());
    }

    @Override
    public void close(GameModel game, WunumModel wunum) {
        close(game, wunum.getIssue());
    }

    private void close(GameModel game, long i) {
        String issue = numeric.toString(i + 1);
        JSONArray array = new JSONArray();
        betDao.query(game.getId(), issue, 0).getList().forEach(bet -> {
            JSONObject object = modelHelper.toJson(bet);
            object.put("player", playerService.getNickAvatar(bet.getPlayer()));
            array.add(object);
        });
        JSONObject object = new JSONObject();
        object.put("issue", issue);
        object.put("sayhias", array);
        messageService.save(game.getId(), "", MessageService.bets, object.toString(SerializerFeature.DisableCircularReferenceDetect));
    }

    @Override
    public Map<String, Long> sumScAmountProfit(long issue) {
        GameModel game = gameService.find(7);
        if (game == null)
            return null;

        return sumScAmountProfit(Set.of(game.getId()), issue);
    }

    private Map<String, Long> sumScAmountProfit(Set<String> game, long issue) {
        Map<String, Long> map = new ConcurrentHashMap<>();
        long amount = 0;
        for (BetModel bet : betDao.query(numeric.toString(issue), 0, 0).getList()) {
            if (!game.contains(bet.getGame()))
                continue;

            amount += bet.getAmount();
            String key = bet.getType() + "/" + bet.getItem();
            map.put(key, map.getOrDefault(key, 0L) + (long) bet.getAmount() * bet.getRate() / 100 - bet.getAmount());
        }
        if (amount > 0 && !map.isEmpty())
            map.put("amount", amount);

        return map;
    }

    @Override
    public Map<String, Long> sumWuAmountProfit(long issue) {
        return null;
    }

    @Override
    public void over(FootballModel football) {
//        GameModel game = gameService.find(9);
        Map<String, Integer> commissions = new HashMap<>();
        betDao.query(football.getGroup() + ":" + football.getGid(), 0, 0).getList().forEach(bet -> {
            int shang = bet.getType().indexOf("-shang");
            int rate = shang == -1 ? footballRate(football, bet, bet.getType(), football.getScoreH(), football.getScoreC())
                    : footballRate(football, bet, bet.getType().substring(0, shang), football.getScoreShangH(), football.getScoreShangC());
//            switch (bet.getType()) {
//                case "rang-qiu": {
//                    int score = football.getStrong().equals("H") ? (football.getScoreH() - football.getScoreC()) : (football.getScoreC() - football.getScoreH());
//                    boolean strong = bet.getItem().equals(football.getStrong());
//                    int indexOf = bet.getSubitem().indexOf('/');
//                    if (indexOf == -1) {
//                        double subitem = numeric.toDouble(bet.getSubitem());
//                        if ((strong && score > subitem) || (!strong && score < subitem))
//                            rate = 100 + bet.getRate();
//                        else if (score == subitem)
//                            rate = bet.getItem().equals(football.getStrong()) ? 50 : (100 + bet.getRate() / 2);
//                    } else {
//                        double[] fs = new double[]{numeric.toDouble(bet.getSubitem().substring(0, indexOf).trim()),
//                                numeric.toDouble(bet.getSubitem().substring(indexOf + 1).trim())};
//                        if ((strong && score > fs[1]) || (!strong && score < fs[0]))
//                            rate = 100 + bet.getRate();
//                        else if (strong && score == fs[0])
//                            rate = 50;
//                        else if (!strong && score == fs[1])
//                            rate = 100 + bet.getRate() / 2;
//                    }
//                    break;
//                }
//                case "de-fen": {
//                    boolean da = bet.getSubitem().startsWith("大");
//                    String fen = bet.getSubitem().substring(1).trim();
//                    int indexOf = fen.indexOf('/');
//                    int sum = football.getScoreH() + football.getScoreC();
//                    if (indexOf == -1) {
//                        double f = numeric.toDouble(fen);
//                        if ((da && sum > f) || (!da && sum < f))
//                            rate = 100 + bet.getRate();
//                    } else {
//                        double[] fs = new double[]{numeric.toDouble(fen.substring(0, indexOf).trim()), numeric.toDouble(fen.substring(indexOf + 1).trim())};
//                        if ((da && sum > fs[1]) || (!da && sum < fs[0]))
//                            rate = 100 + bet.getRate();
//                        else if ((da && sum > fs[0]) || (!da && sum < fs[1]))
//                            rate = 50 + bet.getRate() / 2;
//                    }
//                    break;
//                }
//                case "du-ying":
//                    if ((bet.getItem().equals("H") && football.getScoreH() > football.getScoreC())
//                            || (bet.getItem().equals("C") && football.getScoreH() < football.getScoreC())
//                            || (bet.getItem().equals("He") && football.getScoreH() == football.getScoreC())) {
//                        rate = bet.getRate();
//                    }
//                    break;
//            }
            if (rate > 0) {
                bet.setProfit(Math.round(0.01f * bet.getAmount() * rate));
                playerService.win(bet.getPlayer(), bet.getProfit(),
                        bet.getMemo().replaceFirst("下注", "开奖") + ",中奖金额[" + bet.getProfit() / 100 + "]元");
            }
            bet.setOpen(football.getScoreH() + "-" + football.getScoreC());
            bet.setStatus(1);
            betDao.save(bet);
            commissions.put(bet.getPlayer(), commissions.getOrDefault(bet.getPlayer(), 0) + bet.getCommission());
        });
        sum();
        playerService.commission(commissions);
    }

    private int footballRate(FootballModel football, BetModel bet, String type, int scoreH, int scoreC) {
        switch (type) {
            case "rang-qiu" -> {
                int score = (football.getStrong().equals("H") ? (scoreH - scoreC) : (scoreC - scoreH)) * 100;
                boolean strong = bet.getItem().equals(football.getStrong());
                int indexOf = bet.getSubitem().indexOf('/');
                if (indexOf == -1) {
                    int subitem = footballSubitem(bet.getSubitem());
                    if ((strong && score > subitem) || (!strong && score < subitem))
                        return 100 + bet.getRate();

                    if (score == subitem)
                        return 100; //bet.getItem().equals(football.getStrong()) ? 50 : (100 + bet.getRate() / 2);
                } else {
                    int[] fs = new int[]{footballSubitem(bet.getSubitem().substring(0, indexOf).trim()),
                            footballSubitem(bet.getSubitem().substring(indexOf + 1).trim())};
                    if ((strong && score > fs[1]) || (!strong && score < fs[0]))
                        return 100 + bet.getRate();

                    if (strong && score == fs[0])
                        return 50;

                    if (!strong && score == fs[1])
                        return 100 + bet.getRate() / 2;
                }
            }
            case "de-fen" -> {
                boolean da = bet.getSubitem().startsWith("大");
                String fen = bet.getSubitem().substring(1).trim();
                int indexOf = fen.indexOf('/');
                int sum = scoreH + scoreC;
                if (indexOf == -1) {
                    double f = numeric.toDouble(fen);
                    if ((da && sum > f) || (!da && sum < f))
                        return 100 + bet.getRate();
                } else {
                    double[] fs = new double[]{numeric.toDouble(fen.substring(0, indexOf).trim()), numeric.toDouble(fen.substring(indexOf + 1).trim())};
                    if ((da && sum > fs[1]) || (!da && sum < fs[0]))
                        return 100 + bet.getRate();

                    if ((da && sum > fs[0]) || (!da && sum < fs[1]))
                        return 50 + bet.getRate() / 2;
                }
            }
            case "du-ying" -> {
                if ((bet.getItem().equals("H") && scoreH > scoreC)
                        || (bet.getItem().equals("C") && scoreH < scoreC)
                        || (bet.getItem().equals("He") && scoreH == scoreC)) {
                    return bet.getRate();
                }
            }
            case "bo-dan" -> {
                if (numeric.toInt(bet.getItem().substring(0, 1)) == scoreH && numeric.toInt(bet.getItem().substring(2, 3)) == scoreC)
                    return bet.getRate();
            }
        }

        return 0;
    }

    private int footballSubitem(String subitem) {
        return (int) Math.round(numeric.toDouble(subitem) * 100);
    }

    @Override
    public int cancel(String gid) {
        GameModel game = gameService.get(gid);
        if (game == null)
            return 1;

        JSONObject latest = null;
        if (game.getType() <= 5) {
            latest = pcnumService.latest(gid);
        } else if (game.getType() <= 9) {
            latest = scnumService.latest(gid);
        } else if (game.getType() == 10) {
            latest = wunumService.latest(gid);
        }
        if (latest == null)
            return 1;

        if (latest.getLongValue("close") <= 0 || !latest.containsKey("next"))
            return 2;

        String issue = numeric.toString(latest.getLongValue("next"));
        List<BetModel> list = betDao.query(gid, userService.id(), issue, 0).getList();
        if (list.isEmpty())
            return 3;

        for (BetModel bet : list) {
            betDao.delete(bet);
            playerService.unbet(bet.getPlayer(), bet.getAmount(), bet.getMemo() + ",撤单");
        }

        return 0;
    }

    @Override
    public int zhuihao(String gid, long issue, String type, String name, int amount, int count, int multiple, int stop) {
        GameModel game = gameService.get(gid);
        if (game == null || count < 1 || multiple < 1 || stop < 0 || stop > 1 || game.getType() > 10)
            return 1;

        if (amount < game.getMin())
            return 4;

        if ((game.getType() <= 5 && pcnumService.close(game, issue)) || (game.getType() >= 6 && game.getType() <= 9 && scnumService.close(game, issue))
                || (game.getType() == 10 && wunumService.close(game, issue)))
            return 2;

        RateModel rate = rateService.find(game.getId(), type, name);
        if (rate == null || rate.getAmount() <= 0)
            return 3;

        int sum = 0;
        for (int i = 0; i < count; i++) {
            sum += Math.pow(multiple, i);
        }
        sum *= amount * 100;
        String player = userService.id();
        if (playerService.noEnough(player, sum))
            return 7;

        String zhuihao = generateZhuihao();
        if (zhuihao == null)
            return 1;

        JSONArray items = new JSONArray();
        for (int i = 0; i < count; i++) {
            BetModel bet = new BetModel();
            bet.setAmount(amount * ((int) Math.pow(multiple, i)) * 100);
            bet.setIssue(numeric.toString(issue + i));
            bet.setMemo("追号投注,[" + game.getName() + "]第[" + bet.getIssue() + "]期,投注内容[" + type + "/" + name + "],金额[" + bet.getAmount() / 100 + "]元");
            if (!playerService.bet(player, bet.getAmount(), bet.getMemo()))
                return 7;

            bet.setGame(game.getId());
            bet.setPlayer(player);
            bet.setType(type);
            bet.setItem(name);
            bet.setRate(rate.getAmount());
            bet.setWater(bet.getAmount() * game.getWater() / 10000);
            if (playerService.hasInvitor(player))
                bet.setCommission(bet.getAmount() * game.getCommission() / 10000);
            bet.setStop(stop);
            bet.setZhuihao(zhuihao);
            bet.setTime(dateTime.now());
            betDao.save(bet);

            JSONObject object = new JSONObject();
            object.put("type", type);
            object.put("name", name);
            object.put("amount", bet.getAmount() / 100);
            items.add(object);
        }

        JSONObject object = new JSONObject();
        if (game.getType() <= 5)
            object.put("type", "pc");
        else if (game.getType() >= 6 && game.getType() <= 9)
            object.put("type", "sc");
        else if (game.getType() == 10)
            object.put("type", "wu");
        object.put("issue", issue);
        object.put("items", items);
        object.put("sum", sum);
        messageService.save(game.getId(), player, MessageService.bet, object.toJSONString());

        return 0;
    }

    private String generateZhuihao() {
        for (int i = 0; i < 1024; i++) {
            String zhuihao = generator.random(16);
            if (betDao.count(zhuihao) == 0)
                return zhuihao;
        }

        return null;
    }

    @Override
    public JSONObject sum1(String uid, String nick, String game, String time) {
        return sum(uid, nick, null, game, time, false);
    }

    @Override
    public JSONObject sum2(String uid, String nick, String invitor, String game, String time, int timeType) {
        if (validator.isEmpty(time) && timeType >= 0) {
            Calendar calendar = Calendar.getInstance();
            if (timeType == 0) {
                time = dateTime.toString(dateTime.getStart(calendar.getTime())) + "," + dateTime.toString(dateTime.getEnd(calendar.getTime()));
            } else if (timeType == 1) {
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                time = dateTime.toString(dateTime.getStart(calendar.getTime())) + "," + dateTime.toString(dateTime.getEnd(calendar.getTime()));
            } else if (timeType == 2) {
                time = "," + dateTime.toString(dateTime.getEnd(calendar.getTime()));
                calendar.add(Calendar.DAY_OF_MONTH, -7);
                time = dateTime.toString(dateTime.getStart(calendar.getTime())) + time;
            }
        }

        return sum(uid, nick, invitor, game, time, true);
    }

    private JSONObject sum(String uid, String nick, String iuid, String game, String time, boolean full) {
        Timestamp start;
        Timestamp end;
        if (validator.isEmpty(time)) {
            Date today = dateTime.today();
            start = dateTime.getStart(today);
            end = dateTime.getEnd(today);
        } else {
            String[] t = time.split(",");
            start = dateTime.toTime(t[0]);
            end = dateTime.toTime(t[1]);
        }

        Map<String, int[]> map = new HashMap<>();
        betDao.sum(playerService.ids(uid, nick, iuid), game, 1, 0, start, end).forEach(list -> {
            String player = (String) list.get(0);
            int[] ns = map.computeIfAbsent(player, key -> new int[5]);
            ns[0]++;
            for (int i = 1; i < ns.length; i++)
                ns[i] += numeric.toInt(list.get(i + 1));
        });
        List<PlayerModel> players = new ArrayList<>();
        Map<String, Integer> invitors = new HashMap<>();
        Set<String> set = new HashSet<>();
        map.forEach((id, ns) -> {
            PlayerModel player = playerService.findById(id);
            if (!set.contains(id)) {
                players.add(player);
                set.add(id);
            }
            if (full && ns[4] > 0 && validator.isId(player.getInvitor()))
                invitors.put(player.getInvitor(), invitors.getOrDefault(player.getInvitor(), 0) + ns[4]);
            ns[4] = 0;
        });
        if (validator.isEmpty(iuid)) {
            invitors.forEach((id, commission) -> {
                PlayerModel player = playerService.findById(id);
                if (player == null)
                    return;

                if (!set.contains(id)) {
                    players.add(player);
                    set.add(id);
                }
                map.computeIfAbsent(player.getId(), key -> new int[5])[4] = commission;
            });
        }
        List<JSONObject> list = new ArrayList<>();
        Map<String, int[]> water = profitService.water(new Date(start.getTime()), new Date(end.getTime()));
        players.forEach(player -> {
            JSONObject object = new JSONObject();
            object.put("uid", player.getUid());
            object.put("player", playerService.get(player.getId()));
            int[] ns = map.get(player.getId());
            int[] ws = water.getOrDefault(player.getId(), new int[2]);
            object.put("issues", ns[0]);
            object.put("amounts", ns[1]);
            object.put("profits", ns[2]);
            object.put("gains", ns[2] - ns[1]);
            object.put("waters", ws[0]);
            object.put("waters0", ws[1]);
            object.put("commissions", ns[4]);
            if (full) {
                object.put("deposits", depositService.sum(player.getId(), false, start, end));
                object.put("withdraws", withdrawService.sum(player.getId(), start, end));
                if (validator.isId(player.getInvitor())) {
                    PlayerModel invitor = playerService.findById(player.getInvitor());
                    if (invitor != null)
                        object.put("invitor", invitor.getUid());
                }
            }
            list.add(object);
        });
        String sort = request.get(ConsoleHelper.SORT);
        boolean desc = false;
        if (validator.isEmpty(sort) || sort.indexOf(' ') == -1) {
            sort = "uid";
        } else {
            if (sort.endsWith("descend"))
                desc = true;
            sort = sort.substring(0, sort.indexOf(' '));
        }
        String s = sort;
        boolean d = desc;
        list.sort((o1, o2) -> {
            if (!o1.containsKey(s))
                return 1;

            if (!o2.containsKey(s))
                return -1;

            if ("uid,invitor".contains(s))
                return d ? o2.getString(s).compareTo(o1.getString(s)) : o1.getString(s).compareTo(o2.getString(s));

            return d ? o2.getIntValue(s) - o1.getIntValue(s) : o1.getIntValue(s) - o2.getIntValue(s);
        });

        PageList<?> pl = BeanFactory.getBean(PageList.class);
        JSONObject object = pl.setPage(players.size(), pagination.getPageSize(20), pagination.getPageNum()).toJson();
        JSONArray array = new JSONArray();
        for (int i = pl.getPage() * (pl.getNumber() - 1), size = Math.min(i + pl.getSize(), players.size()); i < size; i++) {
            array.add(list.get(i));
        }
        object.put("list", array);

        return object;
    }

    @Override
    public JSONObject total(String gid) {
        JSONObject object = new JSONObject();
        if (!validator.isId(gid))
            return object;

        GameModel game = gameService.get(gid);
        if (game == null)
            return object;

        JSONArray bets = new JSONArray();
        betDao.sum(gid, "issue", 0).forEach(list -> {
            JSONObject bet = new JSONObject();
            bets.add(bet);
        });

        return object;
    }

    @Override
    public void clear(String game, String issue) {
        betDao.clear(game, issue, 0);
    }

    @Override
    public void overdue(Timestamp time) {
        betDao.delete(1, time);
    }

    @Override
    public void executeHourJob() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        betDao.deleteRobot(1, new Timestamp(calendar.getTimeInMillis()));
    }
}
