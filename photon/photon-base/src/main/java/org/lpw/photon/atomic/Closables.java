package org.lpw.photon.atomic;

public interface Closables {
    /**
     * 关闭所有可关闭事务。
     */
    void close();
}
