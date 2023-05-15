package org.lpw.photon.ctrl.execute;

/**
 * 执行配置监听器。
 */
public interface ExecuteListener {
    /**
     * 配置通知。
     *
     * @param classExecute  类定义。
     * @param methodExecute 方法定义。
     * @param executor      执行器。
     */
    void definition(Execute classExecute, Execute methodExecute, Executor executor);
}
