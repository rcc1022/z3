package org.lpw.photon.atomic;

/**
 * 可失败事务。
 */
public interface Failable {
    /**
     * 失败。
     *
     * @param throwable 异常。
     */
    void fail(Throwable throwable);
}
