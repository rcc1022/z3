package org.lpw.photon.scheduler;

import org.lpw.photon.atomic.Closables;
import org.lpw.photon.atomic.Failable;
import org.lpw.photon.util.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;
import java.util.Set;
import java.util.TimerTask;

@Component("photon.scheduler.task")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SchedulerTaskImpl extends TimerTask implements SchedulerTask {
    @Inject
    private Logger logger;
    @Inject
    private Optional<Set<Failable>> failables;
    @Inject
    private Closables closables;
    private SchedulerJob job;

    @Override
    public TimerTask setJob(SchedulerJob job) {
        this.job = job;

        return this;
    }

    @Override
    public void run() {
        try {
            if (logger.isDebugEnable())
                logger.debug("开始执行定时任务[{}]。", job.getSchedulerName());

            job.executeSchedulerJob();

            if (logger.isDebugEnable())
                logger.debug("定时任务[{}]执行完成。", job.getSchedulerName());
        } catch (Throwable e) {
            failables.ifPresent(set -> set.forEach(failable -> failable.fail(e)));

            logger.warn(e, "执行定时任务[{}]时发生异常！", job.getSchedulerName());
        }

        closables.close();
    }
}
