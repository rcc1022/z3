package org.lpw.photon.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("photon.util.thread")
public class ThreadImpl implements Thread {
    @Inject
    private Generator generator;
    @Inject
    private Logger logger;

    @Override
    public void sleep(int time, TimeUnit unit) {
        sleep(unit.getTime(time));
    }

    @Override
    public void sleep(int min, int max, TimeUnit unit) {
        sleep(generator.random(unit.getTime(min), unit.getTime(max)));
    }

    private void sleep(long time) {
        try {
            java.lang.Thread.sleep(time);
        } catch (InterruptedException e) {
            logger.warn(e, "线程休眠[{}]时发生异常！", time);
        }
    }
}
