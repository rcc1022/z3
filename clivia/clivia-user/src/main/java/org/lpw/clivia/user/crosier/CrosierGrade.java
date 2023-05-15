package org.lpw.clivia.user.crosier;

/**
 * 角色等级。
 */
public interface CrosierGrade {
    /**
     * 获取等级集。
     *
     * @return 等级集。
     */
    int[] grades();

    /**
     * 获取显示名称前缀。
     *
     * @return 显示名称前缀。
     */
    String name();
}
