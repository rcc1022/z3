package org.lpw.photon.atomic;

/**
 * 可关闭事务。
 */
public interface Closable {
    /**
     * 关闭。
     */
    void close();
}
