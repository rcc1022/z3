package org.lpw.clivia.dao;

/**
 * DAO操作类型。
 *
 */
public enum DaoOperation {
    /**
     * 等于。
     */
    Equals("="),
    /**
     * 不等于。
     */
    NotEquals("<>"),
    /**
     * 小于。
     */
    Less("<"),
    /**
     * 大于。
     */
    Greater(">"),
    /**
     * 小于等于。
     */
    LessEquals("<="),
    /**
     * 大于等于。
     */
    GreaterEquals(">=");

    private String operation;

    DaoOperation(String operation) {
        this.operation = operation + "?";
    }

    /**
     * 获取操作类型。
     *
     * @return 操作类型。
     */
    public String get() {
        return operation;
    }
}
