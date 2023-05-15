package org.lpw.photon.ctrl.template;

/**
 * 模板管理器。用于获取模板实例。
 */
public interface Templates {
    /**
     * JSON模板类型。
     */
    String JSON = "json";

    /**
     * FreeMarker模板类型。
     */
    String FREEMARKER = "freemarker";

    /**
     * String模板类型。
     */
    String STRING = "string";

    /**
     * Stream模板类型。
     */
    String STREAM = "stream";

    /**
     * 获取默认模板实例。
     *
     * @return 默认模板实例。
     */
    Template get();

    /**
     * 获取指定类型的模板实例。
     *
     * @param type 模板类型。
     * @return 模板实例。
     */
    Template get(String type);

    /**
     * 不进行包装。
     *
     * @param bool 是否不进行包装。
     */
    void setNopack(boolean bool);

    /**
     * 获取不进行包装。
     * @return 是否不进行包装，true表示不包装，其他表示包装。
     */
    boolean isNopack();
}
