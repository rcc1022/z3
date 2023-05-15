package com.desert.eagle.football;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.game.OverdueListener;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service(FootballModel.NAME + ".service")
public class FootballServiceImpl implements FootballService, OverdueListener, SecondsJob {
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private Json json;
    @Inject
    private Http http;
    @Inject
    private Generator generator;
    @Inject
    private Message message;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private FootballListener listener;
    @Inject
    private FootballDao footballDao;
    @Value("${" + FootballModel.NAME + ".bets:}")
    private String betUrls;
    private final Set<String> nonZero = Set.of("gid", "league", "teamH", "teamC", "strong", "scoreH", "scoreC", "datetime", "timer");
    private final JSONArray[] ons = {new JSONArray(), new JSONArray(), new JSONArray()};
    private String[] bets = null;
    private final Set<String> failure = new HashSet<>();
    private final String[] types = {"rang-qiu", "de-fen", "du-ying"};

    @Override
    public JSONObject query(int group, String league, String teamH, String teamC, int on) {
        return footballDao.query(group, league, teamH, teamC, on, 999, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject get(int group, String gid) {
        FootballModel football = footballDao.find(group, gid);

        return football == null ? new JSONObject() : modelHelper.toJson(football);
    }

    @Override
    public FootballModel find(int group, String gid) {
        return footballDao.find(group, gid);
    }

    @Override
    public void save(FootballModel football) {
        FootballModel model = validator.isId(football.getId()) ? footballDao.findById(football.getId()) : null;
        if (model == null)
            football.setId(null);
        footballDao.save(football);
    }

    @Override
    public void saves(int group, JSONArray games, JSONArray bodans) {
        if (group < 0 || group >= ons.length || games == null || bodans == null)
            return;

        Map<String, String> map = new HashMap<>();
        for (int i = 0, size = bodans.size(); i < size; i++) {
            JSONObject object = bodans.getJSONObject(i);
            map.put(object.getString("gid"), json.toString(object));
        }

        List<FootballModel> list = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        for (int i = 0, size = games.size(); i < size; i++) {
            JSONObject object = games.getJSONObject(i);
            FootballModel football = modelHelper.fromJson(object, FootballModel.class);
            football.setGroup(group);
            FootballModel model = footballDao.find(group, football.getGid());
            boolean exists = model != null;
            if (exists) {
                football.setId(model.getId());
                football.setScoreShangH(model.getScoreShangH());
                football.setScoreShangC(model.getScoreShangC());
                football.setOn(model.getOn());
            } else
                football.setOn(1);
            boolean half = half(football.getTimer());
            if (exists && zero(object)) {
                model.setSort(i);
                if (half) {
                    model.setScoreShangH(football.getScoreH());
                    model.setScoreShangC(football.getScoreC());
                }
                model.setBoDan(map.getOrDefault(football.getGid(), ""));
                model.setTimestamp(timestamp);
                model.setTimer(football.getTimer());
                model.setZero(1);
                footballDao.save(model);
            } else {
                football.setSort(i);
                if (half) {
                    football.setScoreShangH(football.getScoreH());
                    football.setScoreShangC(football.getScoreC());
                }
                football.setBoDan(map.getOrDefault(football.getGid(), ""));
                football.setTimestamp(timestamp);
                football.setZero(0);
                footballDao.save(football);
            }
            if (football.getOn() == 1)
                list.add(football);
        }
        ons[group] = modelHelper.toJson(list);

        footballDao.overdue(group, 999, timestamp).getList().forEach(football -> {
            football.setSort(999);
            footballDao.save(football);
//            listener.over(football);
        });
    }

    private boolean half(String timer) {
        if (validator.isEmpty(timer))
            return false;

        timer = timer.trim();
        if (timer.equals("半场"))
            return true;

        int n = numeric.toInt(timer);

        return n > 0 && n <= 45;
    }

    private boolean zero(JSONObject object) {
        for (String key : object.keySet()) {
            if (nonZero.contains(key))
                continue;

            if (!validator.isEmpty(object.getString(key)))
                return false;
        }

        return true;
    }

    @Override
    public void on(String id, int on) {
        footballDao.on(id, on);
    }

    @Override
    public void allOnOff(int on) {
        footballDao.ons(on, 999);
        for (int i = 0; i < ons.length; i++)
            ons[i] = modelHelper.toJson(footballDao.query(i, null, null, null, 1, 999, 0, 0).getList());
    }

    @Override
    public void delete(String id) {
        footballDao.delete(id);
    }

    @Override
    public JSONArray ons(int group) {
        return group >= 0 && group < ons.length ? ons[group] : new JSONArray();
    }

    @Override
    public String rate(int group, String gid, String type, String team) {
        FootballModel football = footballDao.find(group, gid);
        if (football == null || football.getSort() >= 999)
            return null;

        return switch (type) {
            case "rang-qiu" -> switch (team) {
                case "H" -> football.getRangQiuH();
                case "C" -> football.getRangQiuC();
                default -> null;
            };
            case "de-fen" -> switch (team) {
                case "H" -> football.getDeFenRateH();
                case "C" -> football.getDeFenRateC();
                default -> null;
            };
            case "du-ying" -> switch (team) {
                case "H" -> football.getDuYingH();
                case "C" -> football.getDuYingC();
                case "He" -> football.getDuYingHe();
                default -> null;
            };
            case "rang-qiu-shang" -> switch (team) {
                case "H" -> football.getRangQiuShangH();
                case "C" -> football.getRangQiuShangC();
                default -> null;
            };
            case "de-fen-shang" -> switch (team) {
                case "H" -> football.getDeFenRateShangH();
                case "C" -> football.getDeFenRateShangC();
                default -> null;
            };
            case "du-ying-shang" -> switch (team) {
                case "H" -> football.getDuYingShangH();
                case "C" -> football.getDuYingShangC();
                case "He" -> football.getDuYingShangHe();
                default -> null;
            };
            case "bo-dan" -> bodan(football, team);
            default -> null;
        };
    }

    private String bodan(FootballModel football, String team) {
        JSONObject object = json.toObject(football.getBoDan());
        String score = "h" + team.replace('-', 'c');

        return json.containsKey(object, score) ? object.getString(score) : null;
    }

    @Override
    public boolean check(String gid, String type, String team, String rate) {
        if (validator.isEmpty(bets))
            bets = betUrls.split(",");
        if (validator.isEmpty(bets))
            return false;

        if (type.contains("-shang"))
            gid = numeric.toString(numeric.toInt(gid) + 1);
        rate = numeric.toString(numeric.toDouble(rate), "#.##");
        String bet = bets[generator.random(0, bets.length - 1)];
        String string = http.post(bet + "/bet-to-hg", null, Map.of("gid", gid, "type", type, "chose", team, "rate", rate));
        JSONObject object = json.toObject(string);
        if (json.containsKey(object, "success") && object.getBooleanValue("success"))
            return true;

        logger.warn(null, "测试下注失败[{}/bet-to-hg:{}:{}:{}:{}:{}]", bet, gid, type, team, rate, string);

        return false;
    }

    @Override
    public JSONObject failure() {
        JSONObject object = new JSONObject();
        object.put("label", message.get(FootballModel.NAME + ".hg"));
        object.put("count", failure.size());

        return object;
    }

    @Override
    public void overdue(Timestamp time) {
        footballDao.delete(time.getTime());
    }

    @Override
    public void executeSecondsJob() {
        over();
        if (validator.isEmpty(bets))
            bets = betUrls.split(",");
        if (validator.isEmpty(bets) || ons.length == 0)
            return;

        int n = (int) ((System.currentTimeMillis() / 1000) % (bets.length * ons.length));
        JSONArray on = ons[n % ons.length];
        if (validator.isEmpty(on))
            return;

        JSONObject object = on.getJSONObject(generator.random(0, on.size() - 1));
        String bet = bets[n / ons.length];
        String string = http.post(bet + "/bet-to-hg-test", null, Map.of("gid", object.getString("gid"),
                "type", types[generator.random(0, types.length - 1)], "chose", generator.random(0, 1) == 0 ? "H" : "C"));
        JSONObject obj = json.toObject(string);
        if (json.containsKey(obj, "success") && obj.getBooleanValue("success"))
            failure.remove(bet);
        else
            failure.add(bet);
        if (!failure.isEmpty())
            logger.warn(null, "皇冠异常{}！", failure);
    }

    private void over(){
        long time=System.currentTimeMillis();
        footballDao.over(999,time- TimeUnit.Minute.getTime(5),time-TimeUnit.Minute.getTime(3)).getList().forEach(listener::over);
    }
}
