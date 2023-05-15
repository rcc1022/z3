package com.desert.eagle.pcnum;

import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Logger;

import java.util.*;

public class PcnumController implements Runnable {
    private final Generator generator;
    private final Logger logger;
    private final int index;
    private final List<PcnumBet> list;
    private final long min;
    private final long max;
    private final List<PcnumController> controllers;
    private final List<int[]> nums;
    boolean found;
    long profit;
    PcnumModel pcnum;

    PcnumController(Generator generator, Logger logger, int index, List<PcnumBet> list, long min, long max, List<int[]> nums, List<PcnumController> controllers) {
        this.generator = generator;
        this.logger = logger;
        this.index = index;
        this.list = new ArrayList<>(list);
        this.min = min;
        this.max = max;
        this.nums = nums;
        this.controllers = controllers;
        profit = Long.MAX_VALUE;
        pcnum = new PcnumModel();
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        long count = 0L;
        int[] ns = null;
        while (!nums.isEmpty()) {
            int[] ns0;
            try {
                ns0 = nums.remove(generator.random(0, nums.size() - 1));
                pcnum.setNum1(ns0[0]);
                pcnum.setNum2(ns0[1]);
                pcnum.setNum3(ns0[2]);
                pcnum.setSum(pcnum.getNum1() + pcnum.getNum2() + pcnum.getNum3());
            } catch (Throwable throwable) {
                continue;
            }

            count++;
            long p = profit();
            if (p >= min && p <= max) {
                found = true;
                profit = p;
                for (PcnumController controller : controllers)
                    controller.nums.clear();
                if (logger.isInfoEnable())
                    logger.info("open-control:{}:命中PC控奖目标，共验证[{}]控奖数据，耗时[{}]秒。", index, count, (System.currentTimeMillis() - time) / 1000.0D);

                return;
            }

            if (Math.abs(p - max) < profit) {
                profit = p;
                ns = ns0;
            }
        }
        if (ns != null) {
            pcnum.setNum1(ns[0]);
            pcnum.setNum2(ns[1]);
            pcnum.setNum3(ns[2]);
            pcnum.setSum(pcnum.getNum1() + pcnum.getNum2() + pcnum.getNum3());
        }
        if (logger.isInfoEnable()) {
            logger.info("open-control:{}:验证[{}]PC控奖数据，未命中控奖目标，耗时[{}]秒。", index, count, (System.currentTimeMillis() - time) / 1000.0D);
        }
    }

    private long profit() {
        Set<String> set = new HashSet<>();
        if (pcnum.getSum() >= 14) {
            set.add("双面/大");
            set.add("双面/大" + (pcnum.getSum() % 2 == 1 ? "单" : "双"));
            if (pcnum.getSum() >= 22)
                set.add("双面/极大");
        } else {
            set.add("双面/小");
            set.add("双面/小" + (pcnum.getSum() % 2 == 1 ? "单" : "双"));
            if (pcnum.getSum() <= 5)
                set.add("双面/极小");
        }
        set.add("特码/" + pcnum.getSum());

        int[] ns = new int[]{pcnum.getNum1(), pcnum.getNum2(), pcnum.getNum3()};
        Arrays.sort(ns);
        if (ns[0] == ns[1] && ns[1] == ns[2]) {
            set.add("特殊/豹子");
        } else if (ns[0] == ns[1] || ns[1] == ns[2]) {
            set.add("特殊/对子");
        } else if ((ns[0] + 1 == ns[1] && ns[1] + 1 == ns[2]) || (ns[0] == 0 && (ns[1] == 1 || ns[1] == 8) && ns[2] == 9)) {
            set.add("特殊/顺子");
        }

        long profit = 0;
        for (PcnumBet bet : list) {
            if (!set.contains(bet.typeItem))
                continue;

            if (set.contains("特码/14")) {
                if (bet.typeItem.equals("双面/大") || bet.typeItem.equals("双面/双")) {
                    if (bet.type == 0)
                        profit += bet.s1314;

                    continue;
                }

                if (bet.typeItem.equals("双面/大双"))
                    continue;
            }

            if (set.contains("特码/13")) {
                if (bet.typeItem.equals("双面/小") || bet.typeItem.equals("双面/单")) {
                    if (bet.type == 0)
                        profit += bet.s1314;

                    continue;
                }

                if (bet.typeItem.equals("双面/小单"))
                    continue;
            }

            if (bet.type == 1 && (set.contains("特殊/豹子") || set.contains("特殊/对子") || set.contains("特殊/顺子"))
                    && (bet.typeItem.contains("大") || bet.typeItem.contains("小") || bet.typeItem.contains("单") || bet.typeItem.contains("双")))
                continue;

            if (bet.type == 2 && (ns[0] == 0 || ns[2] == 9)
                    && (bet.typeItem.contains("大") || bet.typeItem.contains("小") || bet.typeItem.contains("单") || bet.typeItem.contains("双")))
                continue;

            profit += bet.profit;
        }

        return profit;
    }
}
