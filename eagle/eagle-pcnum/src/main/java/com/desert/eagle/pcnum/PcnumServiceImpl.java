package com.desert.eagle.pcnum;

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
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service(PcnumModel.NAME + ".service")
public class PcnumServiceImpl implements PcnumService, SecondsJob, OverdueListener {
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
    private PcnumrListener listener;
    @Inject
    private ControlService controlService;
    @Inject
    private PcnumDao pcnumDao;
    private final Map<String, PcnumModel> next = new ConcurrentHashMap<>();
    private final Map<String, PcnumModel> latest = new ConcurrentHashMap<>();
    private final Set<String> closes = new HashSet<>();
    private final List<int[]> nums = new ArrayList<>();
    private PcnumModel controlPcnum = new PcnumModel();
    private final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    private final int processors = Runtime.getRuntime().availableProcessors();
    private final ExecutorService fixedThreadPool = Executors.newFixedThreadPool(processors);

    @Override
    public JSONObject query(int type, String issue) {
        return pcnumDao.query(type, issue, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray list(int type) {
        return modelHelper.toJson(pcnumDao.query(type, null, pagination.getPageSize(20), pagination.getPageNum()).getList());
    }

    @Override
    public JSONObject latest(String gid) {
        PcnumModel pcnum = findLatest(gid);
        if (pcnum == null || !next.containsKey(gid))
            return new JSONObject();

        GameModel game = gameService.get(gid);
        if (game == null)
            return new JSONObject();

        JSONObject object = modelHelper.toJson(pcnum);

        PcnumModel next = this.next.get(gid);
        long surplus = (next.getTime().getTime() - System.currentTimeMillis()) / 1000;
        object.put("next", next.getIssue());
        object.put("close", surplus <= game.getClose() ? 0 : (surplus - game.getClose()));
        object.put("open", surplus <= 0 ? 0 : surplus);

        return object;
    }

    @Override
    public Map<String, String> nextGameIssue(String game) {
        Map<String, String> map = new HashMap<>();
        if (validator.isEmpty(game))
            next.forEach((key, pcnum) -> map.put(key, numeric.toString(pcnum.getIssue())));
        else if (next.containsKey(game))
            map.put(game, numeric.toString(next.get(game).getIssue()));

        return map;
    }

    @Override
    public boolean close(GameModel game, long issue) {
        if (!next.containsKey(game.getId()))
            return true;

        PcnumModel next = this.next.get(game.getId());

        return next.getIssue() != issue || (next.getTime().getTime() - System.currentTimeMillis()) / 1000 <= game.getClose();
    }

    private PcnumModel findLatest(String gid) {
        return latest.computeIfAbsent(gid, key -> {
            GameModel game = gameService.get(gid);
            if (game == null)
                return null;

            int type = game.getType() / 3;

            return type <= 1 ? pcnumDao.latest(type, 1) : null;
        });
    }

    @Override
    public JSONObject get(int type, long issue) {
        PcnumModel pcnum = pcnumDao.find(type, issue);

        return pcnum == null ? new JSONObject() : modelHelper.toJson(pcnum);
    }

    @Override
    public void save(PcnumModel pcnum) {
        PcnumModel model = pcnumDao.find(pcnum.getType(), pcnum.getIssue());
        pcnum.setId(model == null ? null : model.getId());
        pcnum.setSum(pcnum.getNum1() + pcnum.getNum2() + pcnum.getNum3());
        pcnum.setStatus(0);
        if (validator.isEmpty(pcnum.getTime()))
            pcnum.setTime(dateTime.now());
        pcnumDao.save(pcnum);
    }

    @Override
    public void open(String id) {
        PcnumModel pcnum = pcnumDao.findById(id);
        if (pcnum == null)
            return;

        pcnum.setStatus(1);
        if (pcnum.getTime() == null)
            pcnum.setTime(dateTime.now());
        pcnumDao.save(pcnum);
        listener.newer(pcnum);
    }

    @Override
    public void delete(String id) {
        pcnumDao.delete(id);
    }

    @Override
    public void executeSecondsJob() {
        generateNums();
        self();
        sync();
        latest();
    }

    private void generateNums() {
        if (!nums.isEmpty())
            return;

        singleThreadExecutor.submit(() -> {
            if (!nums.isEmpty())
                return;

            long time = System.currentTimeMillis();
            for (int i = 0; i < 10; i++)
                for (int j = 0; j < 10; j++)
                    for (int k = 0; k < 10; k++)
                        nums.add(new int[]{i, j, k});
            if (logger.isInfoEnable())
                logger.info("open-control:初始化[{}]组PC开奖组合，耗时[{}]秒。", nums.size(), (System.currentTimeMillis() - time) / 1000.0D);
        });
    }

    private void self() {
        int issue = (int) (System.currentTimeMillis() / TimeUnit.Minute.getTime(2));
        PcnumModel model = pcnumDao.find(1, issue);
        if (model != null) {
            if (model.getStatus() == 0) {
                model.setStatus(1);
                model.setTime(dateTime.now());
                pcnumDao.save(model);
            }

            return;
        }

        singleThreadExecutor.submit(() -> {
            if (controlPcnum.getIssue() == issue) {
                controlPcnum.setTime(dateTime.now());
                pcnumDao.save(controlPcnum);
            } else {
                PcnumModel pcnum = new PcnumModel();
                control(pcnum, issue);
                pcnum.setTime(dateTime.now());
                pcnumDao.save(pcnum);
            }
        });

        next1(issue, TimeUnit.Minute.getTime(2));
    }

    private void control(long issue) {
        singleThreadExecutor.submit(() -> {
            if (controlPcnum.getIssue() >= issue)
                return;

            PcnumModel pcnum = new PcnumModel();
            control(pcnum, issue);
            controlPcnum = pcnum;
        });
    }


    private void control(PcnumModel pcnum, long issue) {
        pcnum.setType(1);
        pcnum.setIssue(issue);
        generate(pcnum);
        pcnum.setStatus(1);
        ControlModel control = controlService.find("pc");
        if (control == null)
            return;

        List<PcnumBet> list = listener.sumPcAmountProfit(pcnum.getIssue());
        if (validator.isEmpty(list))
            return;

        long amount = list.remove(list.size() - 1).profit;
        if (control.getMode() == 0 || control.getToWin() > 0)
            control(pcnum, list, 0, amount * (10000 - control.getWinRate()) / 10000);
        else
            control(pcnum, list, amount, amount * (10000 + control.getLoseRate()) / 10000);
    }

    private void generate(PcnumModel pcnum) {
        pcnum.setNum1(generator.random(0, 9));
        pcnum.setNum2(generator.random(0, 9));
        pcnum.setNum3(generator.random(0, 9));
        pcnum.setSum(pcnum.getNum1() + pcnum.getNum2() + pcnum.getNum3());
    }

    private void control(PcnumModel pcnum, List<PcnumBet> list, long min, long max) {
        List<PcnumController> controllers = new ArrayList<>();
        List<Future<?>> futures = new ArrayList<>();
        long time = System.currentTimeMillis();
        List<int[]>[] nums = new List[processors];
        for (int i = 0; i < processors; i++)
            nums[i] = new ArrayList<>();
        for (int i = 0, size = this.nums.size(); i < size; i++)
            nums[i % processors].add(this.nums.get(i));
        if (logger.isInfoEnable())
            logger.info("open-control:复制[{}]组PC开奖组合，耗时[{}]秒，启动线程数[{}]。",
                    this.nums.size(), (System.currentTimeMillis() - time) / 1000.0D, processors);
        time = System.currentTimeMillis();
        for (int i = 0; i < processors; i++) {
            PcnumController controller = new PcnumController(generator, logger, i, list, min, max, nums[i], controllers);
            controllers.add(controller);
            futures.add(fixedThreadPool.submit(controller));
        }
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.warn(e, "open-control:等待PC控奖结果时异常。");
            }
        }
        if (logger.isInfoEnable())
            logger.info("open-control:控奖[{}]组PC开奖组合完成，启动线程数[{}]，耗时[{}]秒。",
                    this.nums.size(), processors, (System.currentTimeMillis() - time) / 1000.0D);

        long profit = Long.MAX_VALUE;
        PcnumModel pc = null;
        for (PcnumController controller : controllers) {
            if (controller.found) {
                pc = controller.pcnum;

                break;
            }

            if (Math.abs(max - controller.profit) < profit) {
                profit = controller.profit;
                pc = controller.pcnum;
            }
        }
        if (pc == null)
            return;

        pcnum.setNum1(pc.getNum1());
        pcnum.setNum2(pc.getNum2());
        pcnum.setNum3(pc.getNum3());
        pcnum.setSum(pcnum.getNum1() + pcnum.getNum2() + pcnum.getNum3());
    }

    private void sync() {
        SyncModel sync = syncService.get(0);
        if (sync == null)
            return;

        PcnumModel next = new PcnumModel();
        next.setIssue(sync.getIssue());
        next.setTime(sync.getTime());
        gameService.list().forEach(game -> {
            if (game.getType() <= 2)
                this.next.put(game.getId(), next);
        });

        PcnumModel prev = new PcnumModel();
        prev.setType(0);
        prev.setIssue(sync.getPrevIssue());
        prev.setTime(sync.getPrevTime());
        PcnumModel pcnum = pcnumDao.find(prev.getType(), prev.getIssue());
        if (pcnum != null) {
            if (pcnum.getStatus() == 0) {
                pcnum.setStatus(1);
                pcnum.setTime(prev.getTime());
                pcnumDao.save(pcnum);
            }

            return;
        }

        int i = 0;
        prev.setNum1(sync.getPrevCode()[i++]);
        prev.setNum2(sync.getPrevCode()[i++]);
        prev.setNum3(sync.getPrevCode()[i]);
        prev.setSum(prev.getNum1() + prev.getNum2() + prev.getNum3());
        prev.setStatus(1);
        pcnumDao.save(prev);
    }

    private void latest() {
        Map<String, PcnumModel> newers = new HashMap<>();
        gameService.list().forEach(game -> {
            if (game.getType() > 5)
                return;

            PcnumModel pcnum = pcnumDao.latest(game.getType() / 3, 1);
            if (pcnum == null)
                return;

            PcnumModel latest = this.latest.get(game.getId());
            if (latest == null) {
                this.latest.put(game.getId(), pcnum);

                return;
            }

            PcnumModel next = this.next.get(game.getId());
            if (next == null && game.getType() >= 3)
                next = next1(latest.getIssue(), TimeUnit.Minute.getTime(2));
            if (next == null)
                return;

            if (pcnum.getIssue() == latest.getIssue()) {
                if ((next.getTime().getTime() - System.currentTimeMillis()) / 1000 <= game.getClose() && !closes.contains(game.getId())) {
                    closes.add(game.getId());
                    messageService.save(game.getId(), "", MessageService.close, numeric.toString(pcnum.getIssue() + 1));
                    listener.close(game, pcnum);
                    if (pcnum.getType() == 1)
                        control(next.getIssue());
                }

                return;
            }

            this.latest.put(game.getId(), pcnum);
            closes.remove(game.getId());
            messageService.save(game.getId(), "", MessageService.open, numeric.toString(pcnum.getIssue() + 1));
            newers.put(pcnum.getId(), pcnum);
        });
        newers.forEach((id, pcnum) -> {
            int profit = listener.newer(pcnum);
            if (pcnum.getType() == 1)
                controlService.profit(0, profit);
        });
    }

    private PcnumModel next1(long issue, long time) {
        PcnumModel next = new PcnumModel();
        next.setIssue(issue + 1);
        next.setTime(new Timestamp(System.currentTimeMillis() + time));
        gameService.list().forEach(game -> {
            if (game.getType() >= 3 && game.getType() <= 5)
                this.next.put(game.getId(), next);
        });

        return next;
    }

    @Override
    public void overdue(Timestamp time) {
        pcnumDao.delete(time);
    }
}
