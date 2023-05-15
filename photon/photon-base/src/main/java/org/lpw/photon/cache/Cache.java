package org.lpw.photon.cache;

import java.util.function.Function;

/**
 * 缓存管理器。
 */
public interface Cache {
    /**
     * 默认类型。
     */
    String TYPE_DEFAULT = null;
    /**
     * Redis缓存。
     */
    String TYPE_REDIS = "redis";

    /**
     * 保存缓存对象。
     *
     * @param key      引用key；如果为空则不缓存。
     * @param value    缓存对象；如果为null则不缓存。
     * @param resident 是否常驻内存，如果是则不被自动清除；否则将根据清除规则被清除。
     */
    void put(String key, Object value, boolean resident);

    /**
     * 保存缓存对象。
     *
     * @param type     缓存类型，为空则使用默认缓存。
     * @param key      引用key；如果为空则不缓存。
     * @param value    缓存对象；如果为null则不缓存。
     * @param resident 是否常驻内存，如果是则不被自动清除；否则将根据清除规则被清除。
     */
    void put(String type, String key, Object value, boolean resident);

    /**
     * 获取缓存对象。
     *
     * @param key 引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T get(String key);

    /**
     * 获取缓存对象。
     *
     * @param type 缓存类型，为空则使用默认缓存。
     * @param key  引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T get(String type, String key);

    /**
     * 判断是否包含缓存信息。
     * 
     * @param <T>   缓存类型。
     * @param key   引用key。
     * @param value 目标值。
     * @return 如果存在则返回true；否则返回false。
     */
    <T> boolean has(String key, T value);

    /**
     * 判断是否包含缓存信息。
     * 
     * @param <T>   缓存类型。
     * @param type  缓存类型，为空则使用默认缓存。
     * @param key   引用key。
     * @param value 目标值。
     * @return 如果存在则返回true；否则返回false。
     */
    <T> boolean has(String type, String key, T value);

    /**
     * 获取缓存对对象，如果不存在则重新计算并缓存。
     *
     * @param key      引用key。
     * @param function 重新计算函数。
     * @param resident 是否常驻内存，如果是则不被自动清除；否则将根据清除规则被清除。
     * @param <T>      缓存对象类型。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T computeIfAbsent(String key, Function<String, T> function, boolean resident);

    /**
     * 获取缓存对对象，如果不存在则重新计算并缓存。
     *
     * @param type     缓存类型，为空则使用默认缓存。
     * @param key      引用key。
     * @param function 重新计算函数。
     * @param resident 是否常驻内存，如果是则不被自动清除；否则将根据清除规则被清除。
     * @param <T>      缓存对象类型。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T computeIfAbsent(String type, String key, Function<String, T> function, boolean resident);

    /**
     * 移除缓存对象。
     *
     * @param key 引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T remove(String key);

    /**
     * 移除缓存对象。
     *
     * @param type 缓存类型，为空则使用默认缓存。
     * @param key  引用key。
     * @return 缓存对象；如果不存在则返回null。
     */
    <T> T remove(String type, String key);
}
