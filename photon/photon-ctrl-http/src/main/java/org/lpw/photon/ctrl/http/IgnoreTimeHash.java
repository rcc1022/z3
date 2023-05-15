package org.lpw.photon.ctrl.http;

/**
 * 忽略时间哈希校验。
 */
public interface IgnoreTimeHash {
    /**
     * 验证当前请求是否忽略时间哈希校验。
     *
     * @return true表示忽略，即不校验时间哈希。
     */
    boolean ignore();
}
