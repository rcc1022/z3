package org.lpw.photon.ctrl.context;

/**
 * Session参数注入器。用于设置Session参数。
 */
public interface SessionAware {
    /**
     * 设置Session适配器。
     *
     * @param adapter Session适配器。
     */
    void set(SessionAdapter adapter);
}
