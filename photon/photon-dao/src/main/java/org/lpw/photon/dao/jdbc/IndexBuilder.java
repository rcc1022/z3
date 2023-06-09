package org.lpw.photon.dao.jdbc;

/**
 * 索引设置构建器。
 */
public class IndexBuilder {
    /**
     * 索引类型。
     */
    public enum Type {
        /**
         * 使用。
         */
        Use,
        /**
         * 忽略。
         */
        Ignore,
        /**
         * 强制使用。
         */
        Force
    }

    /**
     * 索引关键词。
     */
    public enum Key {
        /**
         * KEY。
         */
        Key,
        /**
         * INDEX。
         */
        Index
    }

    /**
     * 索引FOR。
     */
    public enum For {
        /**
         * 不指定。
         */
        Null,
        /**
         * JOIN。
         */
        Join,
        /**
         * ORDER BY。
         */
        Order,
        /**
         * GROUP BY。
         */
        Group
    }

    private StringBuilder sb = new StringBuilder();

    /**
     * 添加索引设置。
     *
     * @param type 类型。
     * @param name 索引名。
     * @return 当前实例。
     */
    public IndexBuilder append(Type type, String name) {
        return append(type, Key.Key, name, For.Null);
    }

    /**
     * 添加索引设置。
     *
     * @param type 类型。
     * @param key  关键词。
     * @param name 索引名。
     * @param f    索引FOR。
     * @return 当前实例。
     */
    public IndexBuilder append(Type type, Key key, String name, For f) {
        sb.append(getType(type)).append(key == Key.Index ? "INDEX" : "KEY").append('(').append(name).append(')').append(getFor(f));

        return this;
    }

    private String getType(Type type) {
        return switch (type) {
            case Ignore -> " IGNORE ";
            case Force -> " FORCE ";
            default -> " USE ";
        };
    }

    private String getFor(For f) {
        return switch (f) {
            case Join -> " FOR JOIN";
            case Group -> " FOR GROUP BY";
            case Order -> " FOR ORDER BY";
            default -> "";
        };
    }

    public String get() {
        return sb.toString();
    }
}
