package org.lpw.clivia.cache;

public interface CacheService {
    /**
     * 删除缓存数据。
     *
     * @param type 缓存类型，为空则使用默认。
     * @param key  引用KEY。
     * @return 如果删除成功则返回true；否则返回false。
     */
    boolean remove(String type, String key);
}
