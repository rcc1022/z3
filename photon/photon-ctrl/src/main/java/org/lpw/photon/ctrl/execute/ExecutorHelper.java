package org.lpw.photon.ctrl.execute;

/**
 * 执行器支持。
 */
public interface ExecutorHelper {
    /**
     * 设置当前线程正在执行的执行器。
     *
     * @param uri URI。
     */
    void set(String uri);

    /**
     * 获取当前线程正在执行的执行器。
     *
     * @return 执行器。
     */
    Executor get();

    /**
     * 获取执行器。
     *
     * @param uri URI。
     * @return 执行器。
     */
    Executor get(String uri);
}
