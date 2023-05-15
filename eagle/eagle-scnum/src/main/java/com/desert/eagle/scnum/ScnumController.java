package com.desert.eagle.scnum;

import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Logger;

import java.util.*;

public class ScnumController implements Runnable {
    private final Generator generator;
    private final Logger logger;
    private final int index;
    private final Map<String, Long> map;
    private final long min;
    private final long max;
    private final List<ScnumController> controllers;
    private final List<int[]> nums;
    boolean found;
    long profit;
    ScnumModel scnum;

    ScnumController(Generator generator, Logger logger, int index, Map<String, Long> map, long min, long max, List<int[]> nums, List<ScnumController> controllers) {
        this.generator = generator;
        this.logger = logger;
        this.index = index;
        this.map = new HashMap<>(map);
        this.min = min;
        this.max = max;
        this.nums = nums;
        this.controllers = controllers;
        profit = Long.MAX_VALUE;
        scnum = new ScnumModel();
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
                scnum.setNum1(ns0[0]);
                scnum.setNum2(ns0[1]);
                scnum.setNum3(ns0[2]);
                scnum.setNum4(ns0[3]);
                scnum.setNum5(ns0[4]);
                scnum.setNum6(ns0[5]);
                scnum.setNum7(ns0[6]);
                scnum.setNum8(ns0[7]);
                scnum.setNum9(ns0[8]);
                scnum.setNum10(ns0[9]);
                scnum.setSum(scnum.getNum1() + scnum.getNum2());
            } catch (Throwable throwable) {
                continue;
            }

            count++;
            long p = profit();
            if (p >= min && p <= max) {
                found = true;
                profit = p;
                for (ScnumController controller : controllers)
                    controller.nums.clear();
                if (logger.isInfoEnable())
                    logger.info("open-control:{}:命中赛车控奖目标，共验证[{}]控奖数据，耗时[{}]秒。", index, count, (System.currentTimeMillis() - time) / 1000.0D);

                return;
            }

            if (Math.abs(p - max) < profit) {
                profit = p;
                ns = ns0;
            }
        }
        if (ns != null) {
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
        if (logger.isInfoEnable()) {
            logger.info("open-control:{}:验证[{}]赛车控奖数据，未命中控奖目标，耗时[{}]秒。", index, count, (System.currentTimeMillis() - time) / 1000.0D);
        }
    }

    private long profit() {
        Set<String> set = new HashSet<>();
        win(set, "冠军", scnum.getNum1(), scnum.getNum10());
        win(set, "亚军", scnum.getNum2(), scnum.getNum9());
        win(set, "第三名", scnum.getNum3(), scnum.getNum8());
        win(set, "第四名", scnum.getNum4(), scnum.getNum7());
        win(set, "第五名", scnum.getNum5(), scnum.getNum6());
        win(set, "第六名", scnum.getNum6(), 0);
        win(set, "第七名", scnum.getNum7(), 0);
        win(set, "第八名", scnum.getNum8(), 0);
        win(set, "第九名", scnum.getNum9(), 0);
        win(set, "第十名", scnum.getNum10(), 0);
        set.add("冠亚和/" + scnum.getSum());
        set.add("冠亚和/" + (scnum.getSum() >= 12 ? "大" : "小"));
        set.add("冠亚和/" + (scnum.getSum() % 2 == 1 ? "单" : "双"));

        long profit = 0;
        for (String ti : map.keySet())
            if (set.contains(ti))
                profit += map.get(ti);

        return profit;
    }

    private void win(Set<String> set, String type, int n, int lh) {
        set.add(type + "/" + n);
        set.add(type + "/" + (n >= 6 ? "大" : "小"));
        set.add(type + "/" + (n % 2 == 1 ? "单" : "双"));
        if (lh > 0)
            set.add(type + "/" + (n > lh ? "龙" : "虎"));
    }
}
