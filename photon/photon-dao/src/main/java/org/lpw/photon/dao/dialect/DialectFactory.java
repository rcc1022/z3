package org.lpw.photon.dao.dialect;

/**
 * 方言工厂。
 */
public interface DialectFactory {
    /**
     * 获取方言实例。
     *
     * @param name 方言名称。
     * @return 方言实例。
     */
    Dialect get(String name);
}
