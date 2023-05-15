package com.desert.eagle.wunum;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.control.ControlModel;
import com.desert.eagle.control.ControlService;
import com.desert.eagle.game.GameModel;
import com.desert.eagle.game.GameService;
import com.desert.eagle.game.OverdueListener;
import com.desert.eagle.message.MessageService;
import com.desert.eagle.sync.SyncModel;
import com.desert.eagle.sync.SyncService;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.*;
import org.lpw.photon.util.TimeUnit;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.Thread;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;

@Service(WunumModel.NAME + ".service")
public class WunumServiceImpl implements WunumService, SecondsJob, OverdueListener {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private Generator generator;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private SyncService syncService;
    @Inject
    private GameService gameService;
    @Inject
    private MessageService messageService;
    @Inject
    private ControlService controlService;
    @Inject
    private WunumListener listener;
    @Inject
    private WunumDao wunumDao;
    private final Map<String, WunumModel> next = new ConcurrentHashMap<>();
    private final Map<String, WunumModel> latest = new ConcurrentHashMap<>();
    private final Set<String> closes = new HashSet<>();
    private final List<int[]> nums = new ArrayList<>();
    private WunumModel controlWunum = new WunumModel();
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private final int processors = Runtime.getRuntime().availableProcessors();
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(processors);

    @Override
    public JSONObject query(int type, String issue) {
        return wunumDao.query(type, issue, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject latest(String gid) {
        WunumModel wunum = findLatest(gid);
        if (wunum == null || !next.containsKey(gid))
            return new JSONObject();

        GameModel game = gameService.get(gid);
        if (game == null)
            return new JSONObject();

        JSONObject object = modelHelper.toJson(wunum);
        WunumModel next = this.next.get(gid);
        long surplus = (next.getTime().getTime() - System.currentTimeMillis()) / 1000;
        object.put("next", next.getIssue());
        object.put("close", surplus <= game.getClose() ? 0 : (surplus - game.getClose()));
        object.put("open", surplus <= 0 ? 0 : surplus);

        return object;
    }

    @Override
    public JSONArray list(int type) {
        return modelHelper.toJson(wunumDao.query(type, null, pagination.getPageSize(20), pagination.getPageNum()).getList());
    }

    @Override
    public Map<String, String> nextGameIssue(String game) {
        Map<String, String> map = new HashMap<>();
        if (validator.isEmpty(game))
            next.forEach((key, wunum) -> map.put(key, numeric.toString(wunum.getIssue())));
        else if (next.containsKey(game))
            map.put(game, numeric.toString(next.get(game).getIssue()));

        return map;
    }

    @Override
    public boolean close(GameModel game, long issue) {
        if (!next.containsKey(game.getId()))
            return true;

        WunumModel next = this.next.get(game.getId());

        return next.getIssue() != issue || (next.getTime().getTime() - System.currentTimeMillis()) / 1000 <= game.getClose();
    }

    private WunumModel findLatest(String gid) {
        return latest.computeIfAbsent(gid, key -> {
            GameModel game = gameService.get(gid);
            if (game == null)
                return null;

            int type = game.getType() - 10;

            return type <= 1 ? wunumDao.latest(type, 1) : null;
        });
    }

    @Override
    public JSONObject get(int type, long issue) {
        WunumModel wunum = wunumDao.find(type, issue);

        return wunum == null ? new JSONObject() : modelHelper.toJson(wunum);
    }

    @Override
    public void save(WunumModel wunum) {
        WunumModel model = wunumDao.find(wunum.getType(), wunum.getIssue());
        wunum.setId(model == null ? null : model.getId());
        wunum.setSum(wunum.getNum1() + wunum.getNum2() + wunum.getNum3() + wunum.getNum4() + wunum.getNum5());
        wunum.setStatus(0);
        if (validator.isEmpty(wunum.getTime()))
            wunum.setTime(dateTime.now());
        wunumDao.save(wunum);
    }

    @Override
    public void open(String id) {
        WunumModel wunum = wunumDao.findById(id);
        if (wunum == null)
            return;

        wunum.setStatus(1);
        if (wunum.getTime() == null)
            wunum.setTime(dateTime.now());
        wunumDao.save(wunum);
        listener.newer(wunum);
    }

    @Override
    public void delete(String id) {
        wunumDao.delete(id);
    }

    @Override
    public void executeSecondsJob() {
//        generateNums();
        sync(10);
        sync(11);
//        self();
        latest();
    }

    private void generateNums() {
        if (!nums.isEmpty())
            return;

        singleThreadExecutor.submit(() -> {
            if (!nums.isEmpty())
                return;

            long time = System.currentTimeMillis();
            long[] base = new long[]{1000000000L, 100000000L, 10000000L, 1000000L, 100000L, 10000L, 1000L, 100L, 10L, 1L};
            for (long i = 123456789L; i <= 9876543210L; i++) {
                long[] ls = new long[10];
                Set<Long> set = new HashSet<>();
                for (int j = 0; j < ls.length; j++) {
                    ls[j] = i / base[j] % 10;
                    set.add(ls[j]);
                    if (set.size() <= j) {
                        i += base[j];
                        i -= i % base[j] + 1;

                        break;
                    }
                }
                if (set.size() == ls.length) {
                    int[] ns = new int[ls.length];
                    for (int j = 0; j < ls.length; j++)
                        ns[j] = (int) ls[j] + 1;
                    nums.add(ns);
                }
            }
            if (logger.isInfoEnable())
                logger.info("open-control:初始化[{}]组赛车开奖组合，耗时[{}]秒。", nums.size(), (System.currentTimeMillis() - time) / 1000.0D);
        });
    }

    private void sync(int type) {
        SyncModel sync = syncService.get(type);
        if (sync == null)
            return;

        GameModel game = gameService.find(type);
        if (game == null)
            return;

        WunumModel next = new WunumModel();
        next.setIssue(sync.getIssue());
        next.setTime(sync.getTime());
        this.next.put(game.getId(), next);

        WunumModel prev = new WunumModel();
        prev.setType(type - 10);
        prev.setIssue(sync.getPrevIssue());
        prev.setTime(sync.getPrevTime());
        WunumModel wunum = wunumDao.find(prev.getType(), prev.getIssue());
        if (wunum != null) {
            if (wunum.getStatus() == 0) {
                wunum.setStatus(1);
                wunum.setTime(prev.getTime());
                wunumDao.save(wunum);
            }

            return;
        }

        int i = 0;
        prev.setNum1(sync.getPrevCode()[i++]);
        prev.setNum2(sync.getPrevCode()[i++]);
        prev.setNum3(sync.getPrevCode()[i++]);
        prev.setNum4(sync.getPrevCode()[i++]);
        prev.setNum5(sync.getPrevCode()[i]);
        prev.setSum(prev.getNum1() + prev.getNum2() + prev.getNum3() + prev.getNum4() + prev.getNum5());
        prev.setStatus(1);
        wunumDao.save(prev);
    }

    private void self() {
        self(1, TimeUnit.Minute.getTime(2));
    }

    private void self(int type, long time) {
        long issue = System.currentTimeMillis() / time;
        WunumModel model = wunumDao.find(type, issue);
        if (model != null) {
            if (model.getStatus() == 0) {
                model.setStatus(1);
                model.setTime(dateTime.now());
                wunumDao.save(model);
            }

            return;
        }

        singleThreadExecutor.submit(() -> {
            if (controlWunum.getIssue() == issue) {
                controlWunum.setTime(dateTime.now());
                wunumDao.save(controlWunum);
            } else {
                WunumModel wunum = new WunumModel();
                control(wunum, issue);
                wunum.setTime(dateTime.now());
                wunumDao.save(wunum);
            }
        });

        next7(type, issue, time);
    }

    private void control(long issue) {
        singleThreadExecutor.submit(() -> {
            WunumModel wunum = new WunumModel();
            control(wunum, issue);
            controlWunum = wunum;
        });
    }


    private void control(WunumModel wunum, long issue) {
        wunum.setType(1);
        wunum.setIssue(issue);
        generate(wunum);
        wunum.setStatus(1);
        ControlModel control = controlService.find("sc");
        if (control == null)
            return;

        Map<String, Long> map = listener.sumWuAmountProfit(wunum.getIssue());
        if (validator.isEmpty(map))
            return;

        long amount = map.get("amount");
        if (control.getMode() == 0 || control.getToWin() > 0)
            control(wunum, map, 0, amount * (10000 - control.getWinRate()) / 10000);
        else
            control(wunum, map, amount, amount * (10000 + control.getLoseRate()) / 10000);
    }

    private void generate(WunumModel wunum) {
        wunum.setNum1(generator.random(0, 9));
        wunum.setNum2(generator.random(0, 9));
        wunum.setNum3(generator.random(0, 9));
        wunum.setNum4(generator.random(0, 9));
        wunum.setNum5(generator.random(0, 9));
        wunum.setSum(wunum.getNum1() + wunum.getNum2() + wunum.getNum3() + wunum.getNum4() + wunum.getNum5());
    }

    private void control(WunumModel wunum, Map<String, Long> map, long min, long max) {
        List<WunumController> controllers = new ArrayList<>();
        List<Future<?>> futures = new ArrayList<>();
        long time = System.currentTimeMillis();
        List<int[]>[] nums = new List[processors];
        for (int i = 0; i < processors; i++)
            nums[i] = new ArrayList<>();
        for (int i = 0, size = this.nums.size(); i < size; i++)
            nums[i % processors].add(this.nums.get(i));
        if (logger.isInfoEnable())
            logger.info("open-control:复制[{}]组赛车开奖组合，耗时[{}]秒，启动线程数[{}]。",
                    this.nums.size(), (System.currentTimeMillis() - time) / 1000.0D, processors);
        time = System.currentTimeMillis();
        for (int i = 0; i < processors; i++) {
            WunumController controller = new WunumController(generator, logger, i, map, min, max, nums[i], controllers);
            controllers.add(controller);
            futures.add(fixedThreadPool.submit(controller));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.warn(e, "open-control:等待赛车控奖结果时异常。");
            }
        }
        if (logger.isInfoEnable())
            logger.info("open-control:控奖[{}]组赛车开奖组合完成，启动线程数[{}]，耗时[{}]秒。",
                    this.nums.size(), processors, (System.currentTimeMillis() - time) / 1000.0D);

        long profit = Long.MAX_VALUE;
        WunumModel wu = null;
        for (WunumController controller : controllers) {
            if (controller.found) {
                wu = controller.wunum;

                break;
            }

            if (Math.abs(max - controller.profit) < profit) {
                profit = controller.profit;
                wu = controller.wunum;
            }
        }
        if (wu == null)
            return;

        wunum.setNum1(wu.getNum1());
        wunum.setNum2(wu.getNum2());
        wunum.setNum3(wu.getNum3());
        wunum.setNum4(wu.getNum4());
        wunum.setNum5(wu.getNum5());
        wunum.setSum(wunum.getNum1() + wunum.getNum2());
    }

    private WunumModel next7(int type, long issue, long time) {
        WunumModel next = new WunumModel();
        next.setIssue(issue + 1);
        next.setTime(new Timestamp(System.currentTimeMillis() + time));
        gameService.list().forEach(game -> {
            if (game.getType() - 6 == type)
                this.next.put(game.getId(), next);
        });

        return next;
    }

    private void latest() {
        Map<String, WunumModel> newers = new HashMap<>();
        gameService.list().forEach(game -> {
            if (game.getType() < 10 || game.getType() > 11)
                return;

            WunumModel wunum = wunumDao.latest(game.getType() - 10, 1);
            if (wunum == null)
                return;

            WunumModel latest = this.latest.get(game.getId());
            if (latest == null) {
                this.latest.put(game.getId(), wunum);

                return;
            }

            WunumModel next = this.next.get(game.getId());
            if (next == null && game.getType() == 7)
                next = next7(1, latest.getIssue(), TimeUnit.Minute.getTime(2));
            if (next == null)
                return;

            if (wunum.getIssue() == latest.getIssue()) {
                if ((next.getTime().getTime() - System.currentTimeMillis()) / 1000 <= game.getClose() && !closes.contains(game.getId())) {
                    closes.add(game.getId());
                    messageService.save(game.getId(), "", MessageService.close, numeric.toString(wunum.getIssue() + 1));
                    listener.close(game, wunum);
                    if (wunum.getType() == 1)
                        control(next.getIssue());
                }

                return;
            }

            this.latest.put(game.getId(), wunum);
            closes.remove(game.getId());
            messageService.save(game.getId(), "", MessageService.open, numeric.toString(next.getIssue()));
            newers.put(wunum.getId(), wunum);
        });
        newers.forEach((id, wunum) -> {
            int profit = listener.newer(wunum);
            if (wunum.getType() == 1)
                controlService.profit(1, profit);
        });
    }

    @Override
    public JSONObject getLotteryPksInfo() {
        JSONObject object = new JSONObject();
//        GameModel game = gameService.find(7);
//        if (game == null)
//            return object;
//
//        WunumModel next = this.next.get(game.getId());
//        if (next == null)
//            return object;
//
//        WunumModel wunum = findLatest(game.getId());
//        if (wunum == null)
//            return object;
//
//
//        object.put("errorCode", 0);
//        object.put("message", "操作成功");
//        JSONObject result = new JSONObject();
//        result.put("businessCode", 0);
//        result.put("message", "操作成功");
//        JSONObject data = new JSONObject();
//        data.put("category", "");
//        data.put("drawCount", (System.currentTimeMillis() - dateTime.getStart(dateTime.today()).getTime()) / TimeUnit.Minute.getTime(2) + 1);
//        data.put("drawIssue", next.getIssue());
//        data.put("drawTime", dateTime.toString(next.getTime()));
//        data.put("firstNum", wunum.getNum1());
//        data.put("firstDT", wunum.getNum1() > wunum.getNum10() ? 0 : 1);
//        data.put("secondNum", wunum.getNum2());
//        data.put("secondDT", wunum.getNum2() > wunum.getNum9() ? 0 : 1);
//        data.put("thirdNum", wunum.getNum3());
//        data.put("thirdDT", wunum.getNum3() > wunum.getNum8() ? 0 : 1);
//        data.put("fourthNum", wunum.getNum4());
//        data.put("fourthDT", wunum.getNum4() > wunum.getNum7() ? 0 : 1);
//        data.put("fifthNum", wunum.getNum5());
//        data.put("fifthDT", wunum.getNum5() > wunum.getNum6() ? 0 : 1);
//        data.put("sixthNum", wunum.getNum6());
//        data.put("seventhNum", wunum.getNum7());
//        data.put("eighthNum", wunum.getNum8());
//        data.put("ninthNum", wunum.getNum9());
//        data.put("tenthNum", wunum.getNum10());
//        data.put("frequency", "");
//        data.put("groupCode", 1);
//        data.put("hot", 0);
//        data.put("iconUrl", "");
//        data.put("index", 100);
//        data.put("lotCode", 10037);
//        data.put("lotName", "极速赛车");
//        data.put("lotteryStatus", 0);
//        data.put("preDrawCode", numeric.toString(wunum.getNum1(), "00") + ","
//                + numeric.toString(wunum.getNum2(), "00") + ","
//                + numeric.toString(wunum.getNum3(), "00") + ","
//                + numeric.toString(wunum.getNum4(), "00") + ","
//                + numeric.toString(wunum.getNum5(), "00") + ","
//                + numeric.toString(wunum.getNum6(), "00") + ","
//                + numeric.toString(wunum.getNum7(), "00") + ","
//                + numeric.toString(wunum.getNum8(), "00") + ","
//                + numeric.toString(wunum.getNum9(), "00") + ","
//                + numeric.toString(wunum.getNum10(), "00"));
//        data.put("preDrawIssue", wunum.getIssue());
//        data.put("sumFS", wunum.getSum());
//        data.put("sumBigSamll", wunum.getSum() >= 12 ? 0 : 1);
//        data.put("sumSingleDouble", 1 - (wunum.getSum() % 2));
//        data.put("serverTime", dateTime.toString(dateTime.now()));
//        result.put("data", data);
//        object.put("result", result);

        return object;
    }

    @Override
    public void overdue(Timestamp time) {
        wunumDao.delete(time);
    }
}
