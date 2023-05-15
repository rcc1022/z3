package org.lpw.clivia.dao;

import org.lpw.photon.dao.model.Model;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 检索构建器。
 */
public class QueryBuilder {
    private final LiteOrm liteOrm;
    private final DaoHelper daoHelper;
    private final StringBuilder where;
    private final List<Object> args;
    private String order;

    QueryBuilder(LiteOrm liteOrm, DaoHelper daoHelper) {
        this.liteOrm = liteOrm;
        this.daoHelper = daoHelper;
        where = new StringBuilder();
        args = new ArrayList<>();
    }

    /**
     * 添加WHERE语句。
     *
     * @param column    字段名。
     * @param operation 操作类型。
     * @param value     值，为null或空字符串不添加。
     * @return 当前实例。
     */
    public QueryBuilder where(String column, DaoOperation operation, String value) {
        daoHelper.where(where, args, column, operation, value);

        return this;
    }

    /**
     * 添加WHERE语句。
     *
     * @param column    字段名。
     * @param operation 操作类型。
     * @param value     值，为null或空字符串不添加。
     * @param and       是否必须添加AND。
     * @return 当前实例。
     */
    public QueryBuilder where(String column, DaoOperation operation, String value, boolean and) {
        daoHelper.where(where, args, column, operation, value, and);

        return this;
    }

    /**
     * 添加WHERE语句。
     *
     * @param column    字段名。
     * @param operation 操作类型。
     * @param value     值，小于0不添加。
     * @return 当前实例。
     */
    public QueryBuilder where(String column, DaoOperation operation, long value) {
        daoHelper.where(where, args, column, operation, value);

        return this;
    }

    /**
     * 添加WHERE语句。
     *
     * @param column    字段名。
     * @param operation 操作类型。
     * @param value     值，小于0不添加。
     * @param and       是否必须添加AND。
     * @return 当前实例。
     */
    public QueryBuilder where(String column, DaoOperation operation, long value, boolean and) {
        daoHelper.where(where, args, column, operation, value, and);

        return this;
    }

    /**
     * 添加WHERE语句。
     *
     * @param column    字段名。
     * @param operation 操作类型。
     * @param value     值，null不添加。
     * @return 当前实例。
     */
    public QueryBuilder where(String column, DaoOperation operation, Date value) {
        daoHelper.where(where, args, column, operation, value);

        return this;
    }

    /**
     * 添加WHERE语句。
     *
     * @param column    字段名。
     * @param operation 操作类型。
     * @param value     值，null不添加。
     * @param and       是否必须添加AND。
     * @return 当前实例。
     */
    public QueryBuilder where(String column, DaoOperation operation, Date value, boolean and) {
        daoHelper.where(where, args, column, operation, value, and);

        return this;
    }

    /**
     * 添加WHERE语句。
     *
     * @param column    字段名。
     * @param operation 操作类型。
     * @param value     值，null不添加。
     * @return 当前实例。
     */
    public QueryBuilder where(String column, DaoOperation operation, Timestamp value) {
        daoHelper.where(where, args, column, operation, value);

        return this;
    }

    /**
     * 添加WHERE语句。
     *
     * @param column    字段名。
     * @param operation 操作类型。
     * @param value     值，null不添加。
     * @param and       是否必须添加AND。
     * @return 当前实例。
     */
    public QueryBuilder where(String column, DaoOperation operation, Timestamp value, boolean and) {
        daoHelper.where(where, args, column, operation, value, and);

        return this;
    }

    /**
     * 添加WHERE BETWEEN语句。
     *
     * @param column 字段名。
     * @param type   字段类型。
     * @param value  值。
     * @return 当前实例。
     */
    public QueryBuilder between(String column, ColumnType type, String value) {
        daoHelper.between(where, args, column, type, value);

        return this;
    }

    /**
     * 添加WHERE BETWEEN语句。
     *
     * @param column 字段名。
     * @param type   字段类型。
     * @param value  值。
     * @param and    是否必须添加AND。
     * @return 当前实例。
     */
    public QueryBuilder between(String column, ColumnType type, String value, boolean and) {
        daoHelper.between(where, args, column, type, value, and);

        return this;
    }

    /**
     * 添加WHERE IN语句。
     *
     * @param column 字段名。
     * @param values 值集，为null或空则不添加。
     * @return 当前实例。
     */
    public QueryBuilder in(String column, Set<?> values) {
        daoHelper.in(where, args, column, values);

        return this;
    }

    /**
     * 添加WHERE IN语句。
     *
     * @param column 字段名。
     * @param values 值集，为null或空则不添加。
     * @param and    是否必须添加AND。
     * @return 当前实例。
     */
    public QueryBuilder in(String column, Set<?> values, boolean and) {
        daoHelper.in(where, args, column, values, and);

        return this;
    }

    /**
     * 添加WHERE LIKE语句。
     *
     * @param dataSource 数据源KEY。
     * @param column     字段名。
     * @param value      值，为null或空字符串不添加。
     * @return 当前实例。
     */
    public QueryBuilder like(String dataSource, String column, String value) {
        daoHelper.like(dataSource, where, args, column, value);

        return this;
    }

    /**
     * 添加WHERE LIKE语句。
     *
     * @param dataSource 数据源KEY。
     * @param column     字段名。
     * @param value      值，为null或空字符串不添加。
     * @param and        是否必须添加AND。
     * @return 当前实例。
     */
    public QueryBuilder like(String dataSource, String column, String value, boolean and) {
        daoHelper.like(dataSource, where, args, column, value, and);

        return this;
    }

    /**
     * 添加WHERE LIKE语句。
     *
     * @param dataSource 数据源KEY。
     * @param column     字段名。
     * @param value      值，为null或空字符串不添加。
     * @param prefix     是否模糊匹配前部字符串，即是否在参数值前添加%。
     * @param suffix     是否模糊匹配尾部字符串，即是否在参数值末添加%。
     * @return 当前实例。
     */
    public QueryBuilder like(String dataSource, String column, String value, boolean prefix, boolean suffix) {
        daoHelper.like(dataSource, where, args, column, value, prefix, suffix);

        return this;
    }

    /**
     * 添加WHERE LIKE语句。
     *
     * @param dataSource 数据源KEY。
     * @param column     字段名。
     * @param value      值，为null或空字符串不添加。
     * @param prefix     是否模糊匹配前部字符串，即是否在参数值前添加%。
     * @param suffix     是否模糊匹配尾部字符串，即是否在参数值末添加%。
     * @param and        是否必须添加AND。
     * @return 当前实例。
     */
    public QueryBuilder like(String dataSource, String column, String value, boolean prefix, boolean suffix, boolean and) {
        daoHelper.like(dataSource, where, args, column, value, prefix, suffix, and);

        return this;
    }

    /**
     * 设置ORDER BY片段。为空则不排序。
     *
     * @param order ORDER BY片段。
     * @return 当前实例。
     */
    public QueryBuilder order(String order) {
        this.order = order;

        return this;
    }

    /**
     * 检索。
     *
     * @param modelClass Model类。
     * @param pageSize   最大返回的记录数。
     * @param pageNum    当前显示的页码。
     * @param <T>        Model类型。
     * @return Model实例集。
     */
    public <T extends Model> PageList<T> query(Class<T> modelClass, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(modelClass).where(where.toString()).order(order).size(pageSize).page(pageNum), args.toArray());
    }

    /**
     * 更新。
     *
     * @param modelClass Model类。
     * @param set        SET子句。
     * @param args       SET参数集。
     * @param <T>        Model类型。
     */
    public <T extends Model> void update(Class<T> modelClass, String set, Object[] args) {
        List<Object> list = new ArrayList<>();
        Collections.addAll(list, args);
        list.addAll(this.args);
        liteOrm.update(new LiteQuery(modelClass).set(set).where(where.toString()), list.toArray());
    }

    /**
     * 删除。
     *
     * @param modelClass Model类。
     * @param <T>        Model类型。
     */
    public <T extends Model> void delete(Class<T> modelClass) {
        liteOrm.delete(new LiteQuery(modelClass).where(where.toString()), args.toArray());
    }

    /**
     * 获取WHERE片段。
     *
     * @return WHERE片段。
     */
    public String getWhere() {
        return where.toString();
    }

    /**
     * 获取参数集。
     *
     * @return 参数集。
     */
    public Object[] getArgs() {
        return args.toArray();
    }
}
