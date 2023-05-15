package org.lpw.photon.dao.model;

/**
 * 持久化模型定义。
 */
public interface Model {
    /**
     * 获得Model ID值。
     *
     * @return Model ID值。
     */
    String getId();

    /**
     * 设置Model ID值。
     *
     * @param id Model ID值。
     */
    void setId(String id);
}
