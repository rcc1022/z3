package org.lpw.photon.util;

/**
 * 时间Hash算法与验证。
 */
public interface TimeHash {
    /**
     * 生产当前时间Hash值。
     *
     * @return 当前时间Hash值。
     */
    int generate();

    /**
     * 获取是否可验证时间哈希值。
     *
     * @return 如果可验证则返回true；否则返回false。
     */
    boolean isEnable();

    /**
     * 验证时间Hash值是否有效。
     *
     * @param code 时间Hash值。
     * @return 如果有效则返回true；否则返回false。
     */
    boolean valid(int code);
}
