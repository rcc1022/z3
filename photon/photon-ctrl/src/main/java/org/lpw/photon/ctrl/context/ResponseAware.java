package org.lpw.photon.ctrl.context;

/**
 * 输出参数注入器。用于设置输出参数。
 */
public interface ResponseAware {
    /**
     * 设置输出适配器。
     *
     * @param adapter 输出适配器。
     */
    void set(ResponseAdapter adapter);
}
