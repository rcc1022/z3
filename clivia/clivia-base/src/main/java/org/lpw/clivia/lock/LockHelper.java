package org.lpw.clivia.lock;

import java.util.function.Function;

/**
 * 全局锁主要应用于集群、分布式环境下，对同一关键数据进行悲观锁操作。
 */
public interface LockHelper {
    /**
     * 执行锁定操作。目标操作执行完成后将自动解锁。
     *
     * @param key      锁key。
     * @param wait     等待时长，单位：毫秒。如果已被其他线程锁定则等待。
     * @param alive    有效时长,小于等于0则使用默认值5，单位：秒。如果超过有效时长未解锁的，将被自动解锁。
     * @param function 目标操作，获得锁时执行，如果无法获得锁则不执行。操作参数为锁ID。
     * @param <T>      执行返回结果类型。
     * @return 执行成功则返回执行结果；否则返回null。
     */
    <T> T lock(String key, long wait, int alive, Function<String, T> function);

    /**
     * 锁定。
     *
     * @param key   锁key。
     * @param wait  等待时长，单位：毫秒。如果已被其他线程锁定则等待。
     * @param alive 有效时长,小于等于0则使用默认值5，单位：秒。如果超过有效时长未解锁的，将被自动解锁。
     * @return 锁定ID，如果锁定失败则返回null。
     */
    String lock(String key, long wait, int alive);

    /**
     * 解锁。
     *
     * @param id 锁定ID。
     */
    void unlock(String id);
}
