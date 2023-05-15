package org.lpw.photon.scheduler;

/**
 * 定时任务。
 */
public interface SchedulerJob {
    /**
     * 获取定时任务名称。
     *
     * @return 定时任务名称。
     */
    String getSchedulerName();

    /**
     * 执行定时任务。
     */
    void executeSchedulerJob();
}
