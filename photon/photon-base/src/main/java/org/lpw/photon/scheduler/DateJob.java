package org.lpw.photon.scheduler;

/**
 * 每日执行定时器任务，每日执行一次任务。
 */
public interface DateJob {
    /**
     * 执行每日任务。
     */
    void executeDateJob();
}
