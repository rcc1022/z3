package org.lpw.photon.ctrl.http;

import java.util.Set;

/**
 * 忽略URI地址。
 */
public interface IgnoreUri {
    /**
     * 获取忽略的URI地址集。
     *
     * @return URI地址集。
     */
    Set<String> getIgnoreUris();
}
