package com.desert.eagle.scnum;

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
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.*;

@Service(ScnumModel.NAME + ".service")
public class ScnumServiceImpl implements ScnumService, SecondsJob, OverdueListener {
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
    private ScnumListener listener;
    @Inject
    private ScnumDao scnumDao;
    private final Map<String, ScnumModel> next = new ConcurrentHashMap<>();
    private final Map<String, ScnumModel> latest = new ConcurrentHashMap<>();
    private final Set<String> closes = new HashSet<>();
    private final List<int[]> nums = new ArrayList<>();
    private ScnumModel controlScnum = new ScnumModel();
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private final int processors = Runtime.getRuntime().availableProcessors();
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(processors);

    @Override
    public JSONObject query(int type, String issue) {
        return scnumDao.query(type, issue, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject latest(String gid) {
        ScnumModel scnum = findLatest(gid);
        if (scnum == null || !next.containsKey(gid))
            return new JSONObject();

        GameModel game = gameService.get(gid);
        if (game == null)
            return new JSONObject();

        JSONObject object = modelHelper.toJson(scnum);
        ScnumModel next = this.next.get(gid);
        long surplus = (next.getTime().getTime() - System.currentTimeMillis()) / 1000;
        object.put("next", next.getIssue());
        object.put("close", surplus <= game.getClose() ? 0 : (surplus - game.getClose()));
        object.put("open", surplus <= 0 ? 0 : surplus);

        return object;
    }

    @Override
    public JSONArray list(int type) {
        return modelHelper.toJson(scnumDao.query(type, null, pagination.getPageSize(20), pagination.getPageNum()).getList());
    }

    @Override
    public Map<String, String> nextGameIssue(String game) {
        Map<String, String> map = new HashMap<>();
        if (validator.isEmpty(game))
            next.forEach((key, scnum) -> map.put(key, numeric.toString(scnum.getIssue())));
        else if (next.containsKey(game))
            map.put(game, numeric.toString(next.get(game).getIssue()));

        return map;
    }

    @Override
    public boolean close(GameModel game, long issue) {
        if (!next.containsKey(game.getId()))
            return true;

        ScnumModel next = this.next.get(game.getId());

        return next.getIssue() != issue || (next.getTime().getTime() - System.currentTimeMillis()) / 1000 <= game.getClose();
    }

    private ScnumModel findLatest(String gid) {
        return latest.computeIfAbsent(gid, key -> {
            GameModel game = gameService.get(gid);
            if (game == null)
                return null;

            int type = game.getType() - 6;

            return type <= 3 ? scnumDao.latest(type, 1) : null;
        });
    }

    @Override
    public JSONObject get(int type, long issue) {
        ScnumModel scnum = scnumDao.find(type, issue);

        return scnum == null ? new JSONObject() : modelHelper.toJson(scnum);
    }

    @Override
    public void save(ScnumModel scnum) {
        ScnumModel model = scnumDao.find(scnum.getType(), scnum.getIssue());
        scnum.setId(model == null ? null : model.getId());
        scnum.setSum(scnum.getNum1() + scnum.getNum2());
        scnum.setStatus(0);
        if (validator.isEmpty(scnum.getTime()))
            scnum.setTime(dateTime.now());
        scnumDao.save(scnum);
    }

    @Override
    public void open(String id) {
        ScnumModel scnum = scnumDao.findById(id);
        if (scnum == null)
            return;

        scnum.setStatus(1);
        if (scnum.getTime() == null)
            scnum.setTime(dateTime.now());
        scnumDao.save(scnum);
        listener.newer(scnum);
    }

    @Override
    public void delete(String id) {
        scnumDao.delete(id);
    }

    @Override
    public void executeSecondsJob() {
        generateNums();
        sync(6);
        sync(8);
        sync(9);
        self();
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

        ScnumModel next = new ScnumModel();
        next.setIssue(sync.getIssue());
        next.setTime(sync.getTime());
        this.next.put(game.getId(), next);

        ScnumModel prev = new ScnumModel();
        prev.setType(type - 6);
        prev.setIssue(sync.getPrevIssue());
        prev.setTime(sync.getPrevTime());
        ScnumModel scnum = scnumDao.find(prev.getType(), prev.getIssue());
        if (scnum != null) {
            if (scnum.getStatus() == 0) {
                scnum.setStatus(1);
                scnum.setTime(prev.getTime());
                scnumDao.save(scnum);
            }

            return;
        }

        int i = 0;
        prev.setNum1(sync.getPrevCode()[i++]);
        prev.setNum2(sync.getPrevCode()[i++]);
        prev.setNum3(sync.getPrevCode()[i++]);
        prev.setNum4(sync.getPrevCode()[i++]);
        prev.setNum5(sync.getPrevCode()[i++]);
        prev.setNum6(sync.getPrevCode()[i++]);
        prev.setNum7(sync.getPrevCode()[i++]);
        prev.setNum8(sync.getPrevCode()[i++]);
        prev.setNum9(sync.getPrevCode()[i++]);
        prev.setNum10(sync.getPrevCode()[i]);
        prev.setSum(prev.getNum1() + prev.getNum2());
        prev.setStatus(1);
        scnumDao.save(prev);
    }

    private void self() {
        self(1, TimeUnit.Minute.getTime(2));
    }

    private void self(int type, long time) {
        long issue = System.currentTimeMillis() / time;
        ScnumModel model = scnumDao.find(type, issue);
        if (model != null) {
            if (model.getStatus() == 0) {
                model.setStatus(1);
                model.setTime(dateTime.now());
                scnumDao.save(model);
            }

            return;
        }

        singleThreadExecutor.submit(() -> {
            if (controlScnum.getIssue() == issue) {
                controlScnum.setTime(dateTime.now());
                scnumDao.save(controlScnum);
            } else {
                ScnumModel scnum = new ScnumModel();
                control(scnum, issue);
                scnum.setTime(dateTime.now());
                scnumDao.save(scnum);
            }
        });

        next7(type, issue, time);
    }

    private void control(long issue) {
        singleThreadExecutor.submit(() -> {
            ScnumModel scnum = new ScnumModel();
            control(scnum, issue);
            controlScnum = scnum;
        });
    }


    private void control(ScnumModel scnum, long issue) {
        scnum.setType(1);
        scnum.setIssue(issue);
        generate(scnum);
        scnum.setStatus(1);
        ControlModel control = controlService.find("sc");
        if (control == null)
            return;

        Map<String, Long> map = listener.sumScAmountProfit(scnum.getIssue());
        if (validator.isEmpty(map))
            return;

        long amount = map.get("amount");
        if (control.getMode() == 0 || control.getToWin() > 0)
            control(scnum, map, 0, amount * (10000 - control.getWinRate()) / 10000);
        else
            control(scnum, map, amount, amount * (10000 + control.getLoseRate()) / 10000);
    }

    private void generate(ScnumModel scnum) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            list.add(i + 1);
        }
        int[] ns = new int[10];
        for (int i = 0; i < 10; i++) {
            ns[i] = list.remove(generator.random(0, list.size() - 1));
        }
        scnum.setNum1(ns[0]);
        scnum.setNum2(ns[1]);
        scnum.setNum3(ns[2]);
        scnum.setNum4(ns[3]);
        scnum.setNum5(ns[4]);
        scnum.setNum6(ns[5]);
        scnum.setNum7(ns[6]);
        scnum.setNum8(ns[7]);
        scnum.setNum9(ns[8]);
        scnum.setNum10(ns[9]);
        scnum.setSum(scnum.getNum1() + scnum.getNum2());
    }

    private void control(ScnumModel scnum, Map<String, Long> map, long min, long max) {
        List<ScnumController> controllers = new ArrayList<>();
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
            ScnumController controller = new ScnumController(generator, logger, i, map, min, max, nums[i], controllers);
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
        ScnumModel sc = null;
        for (ScnumController controller : controllers) {
            if (controller.found) {
                sc = controller.scnum;

                break;
            }

            if (Math.abs(max - controller.profit) < profit) {
                profit = controller.profit;
                sc = controller.scnum;
            }
        }
        if (sc == null)
            return;

        scnum.setNum1(sc.getNum1());
        scnum.setNum2(sc.getNum2());
        scnum.setNum3(sc.getNum3());
        scnum.setNum4(sc.getNum4());
        scnum.setNum5(sc.getNum5());
        scnum.setNum6(sc.getNum6());
        scnum.setNum7(sc.getNum7());
        scnum.setNum8(sc.getNum8());
        scnum.setNum9(sc.getNum9());
        scnum.setNum10(sc.getNum10());
        scnum.setSum(scnum.getNum1() + scnum.getNum2());
    }

    private ScnumModel next7(int type, long issue, long time) {
        ScnumModel next = new ScnumModel();
        next.setIssue(issue + 1);
        next.setTime(new Timestamp(System.currentTimeMillis() + time));
        gameService.list().forEach(game -> {
            if (game.getType() - 6 == type)
                this.next.put(game.getId(), next);
        });

        return next;
    }

    private void latest() {
        Map<String, ScnumModel> newers = new HashMap<>();
        gameService.list().forEach(game -> {
            if (game.getType() < 6 || game.getType() > 9)
                return;

            ScnumModel scnum = scnumDao.latest(game.getType() - 6, 1);
            if (scnum == null)
                return;

            ScnumModel latest = this.latest.get(game.getId());
            if (latest == null) {
                this.latest.put(game.getId(), scnum);

                return;
            }

            ScnumModel next = this.next.get(game.getId());
            if (next == null && game.getType() == 7)
                next = next7(1, latest.getIssue(), TimeUnit.Minute.getTime(2));
            if (next == null)
                return;

            if (scnum.getIssue() == latest.getIssue()) {
                if ((next.getTime().getTime() - System.currentTimeMillis()) / 1000 <= game.getClose() && !closes.contains(game.getId())) {
                    closes.add(game.getId());
                    messageService.save(game.getId(), "", MessageService.close, numeric.toString(scnum.getIssue() + 1));
                    listener.close(game, scnum);
                    if (scnum.getType() == 1)
                        control(next.getIssue());
                }

                return;
            }

            this.latest.put(game.getId(), scnum);
            closes.remove(game.getId());
            messageService.save(game.getId(), "", MessageService.open, numeric.toString(next.getIssue()));
            newers.put(scnum.getId(), scnum);
        });
        newers.forEach((id, scnum) -> {
            int profit = listener.newer(scnum);
            if (scnum.getType() == 1)
                controlService.profit(1, profit);
        });
    }

    @Override
    public JSONObject getLotteryPksInfo() {
        JSONObject object = new JSONObject();
        GameModel game = gameService.find(7);
        if (game == null)
            return object;

        ScnumModel next = this.next.get(game.getId());
        if (next == null)
            return object;

        ScnumModel scnum = findLatest(game.getId());
        if (scnum == null)
            return object;


        object.put("errorCode", 0);
        object.put("message", "操作成功");
        JSONObject result = new JSONObject();
        result.put("businessCode", 0);
        result.put("message", "操作成功");
        JSONObject data = new JSONObject();
        data.put("category", "");
        data.put("drawCount", (System.currentTimeMillis() - dateTime.getStart(dateTime.today()).getTime()) / TimeUnit.Minute.getTime(2) + 1);
        data.put("drawIssue", next.getIssue());
        data.put("drawTime", dateTime.toString(next.getTime()));
        data.put("firstNum", scnum.getNum1());
        data.put("firstDT", scnum.getNum1() > scnum.getNum10() ? 0 : 1);
        data.put("secondNum", scnum.getNum2());
        data.put("secondDT", scnum.getNum2() > scnum.getNum9() ? 0 : 1);
        data.put("thirdNum", scnum.getNum3());
        data.put("thirdDT", scnum.getNum3() > scnum.getNum8() ? 0 : 1);
        data.put("fourthNum", scnum.getNum4());
        data.put("fourthDT", scnum.getNum4() > scnum.getNum7() ? 0 : 1);
        data.put("fifthNum", scnum.getNum5());
        data.put("fifthDT", scnum.getNum5() > scnum.getNum6() ? 0 : 1);
        data.put("sixthNum", scnum.getNum6());
        data.put("seventhNum", scnum.getNum7());
        data.put("eighthNum", scnum.getNum8());
        data.put("ninthNum", scnum.getNum9());
        data.put("tenthNum", scnum.getNum10());
        data.put("frequency", "");
        data.put("groupCode", 1);
        data.put("hot", 0);
        data.put("iconUrl", "");
        data.put("index", 100);
        data.put("lotCode", 10037);
        data.put("lotName", "极速赛车");
        data.put("lotteryStatus", 0);
        data.put("preDrawCode", numeric.toString(scnum.getNum1(), "00") + ","
                + numeric.toString(scnum.getNum2(), "00") + ","
                + numeric.toString(scnum.getNum3(), "00") + ","
                + numeric.toString(scnum.getNum4(), "00") + ","
                + numeric.toString(scnum.getNum5(), "00") + ","
                + numeric.toString(scnum.getNum6(), "00") + ","
                + numeric.toString(scnum.getNum7(), "00") + ","
                + numeric.toString(scnum.getNum8(), "00") + ","
                + numeric.toString(scnum.getNum9(), "00") + ","
                + numeric.toString(scnum.getNum10(), "00"));
        data.put("preDrawIssue", scnum.getIssue());
        data.put("sumFS", scnum.getSum());
        data.put("sumBigSamll", scnum.getSum() >= 12 ? 0 : 1);
        data.put("sumSingleDouble", 1 - (scnum.getSum() % 2));
        data.put("serverTime", dateTime.toString(dateTime.now()));
        result.put("data", data);
        object.put("result", result);

        return object;
    }

    @Override
    public void overdue(Timestamp time) {
        scnumDao.delete(time);
    }
}
