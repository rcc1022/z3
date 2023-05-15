package org.lpw.clivia.async;

import java.util.concurrent.Callable;

interface Callabler extends Callable<String> {
    /**
     * 设置执行实例。
     *
     * @param callable      执行实例。
     * @param asyncNotifier 通知器。
     * @return 当前实例。
     */
    Callabler set(Callable<String> callable, AsyncNotifier asyncNotifier);
}
