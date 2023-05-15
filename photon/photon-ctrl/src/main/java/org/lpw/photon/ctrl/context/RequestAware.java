package org.lpw.photon.ctrl.context;

/**
 * 请求参数注入器。用于设置请求参数。
 */
public interface RequestAware {
    /**
     * 设置请求适配器。
     *
     * @param adapter 请求适配器。
     */
    void set(RequestAdapter adapter);
}
