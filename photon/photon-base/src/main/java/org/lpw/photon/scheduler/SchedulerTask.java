package org.lpw.photon.scheduler;

import java.util.TimerTask;

/**
 * 定时任务。
 */
public interface SchedulerTask {
    /**
     * 设置定时任务。
     *
     * @param job 定时任务。
     * @return 定时任务。
     */
    TimerTask setJob(SchedulerJob job);
}
