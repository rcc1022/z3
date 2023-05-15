package org.lpw.photon.dao.auto;

import org.lpw.photon.dao.model.Model;

import java.util.Set;

public interface Executer {
    /**
     * 保存SQL。
     *
     * @param dataSource 数据源。
     * @param sql        执行的SQL。
     * @param state0     是否检查状态0，如果是则仅当已存在数据状态均不为0时执行。
     * @return 影响数据行数。
     */
    int execute(String dataSource, String sql, boolean state0);

    /**
     * 读取create.sql并创建表。
     *
     * @param modelClass Model类。
     * @param tableName  表名。
     */
    void create(Class<? extends Model> modelClass, String tableName);

    /**
     * 获取表名称集。
     *
     * @param dataSource 数据源。
     * @return 表名称集。
     */
    Set<String> tables(String dataSource);
}
