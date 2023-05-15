package org.lpw.photon.dao.orm;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.dao.model.Model;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface PageList<T extends Model> {
    /**
     * 设置分页信息。
     *
     * @param count  记录总数。
     * @param size   每页显示记录数。
     * @param number 当前显示页码数。
     * @return 当前实例。
     */
    PageList<T> setPage(int count, int size, int number);

    /**
     * 获取记录总数。
     *
     * @return 记录总数。
     */
    int getCount();

    /**
     * 获取每页最大显示记录数。
     *
     * @return 每页最大显示记录数。
     */
    int getSize();

    /**
     * 获取当前显示页数。
     *
     * @return 当前显示页数。
     */
    int getNumber();

    /**
     * 获取总页数。
     *
     * @return 总页数。
     */
    int getPage();

    /**
     * 获取分页显示起始页数。
     *
     * @return 起始页数。
     */
    int getPageStart();

    /**
     * 获取分页显示结束页数。
     *
     * @return 结束页数。
     */
    int getPageEnd();

    /**
     * 获取数据集。
     *
     * @return 数据集。
     */
    List<T> getList();

    /**
     * 设置数据集。
     *
     * @param list 数据集。
     */
    void setList(List<T> list);

    /**
     * 过滤并截取数据集。
     * 
     * @param size     每页显示记录数。
     * @param number   当前显示页码数。
     * @param function 过滤器。
     * @return 当前实例。
     */
    PageList<T> subList(int size, int number, Function<T, Boolean> function);

    /**
     * 转化为JSON格式的数据。
     *
     * @return JSON格式的数据。
     */
    JSONObject toJson();

    /**
     * 转化为JSON格式的数据。
     *
     * @param biConsumer 转化器。
     * @return JSON格式的数据。
     */
    JSONObject toJson(BiConsumer<T, JSONObject> biConsumer);

    /**
     * 转化为JSON格式的数据。
     *
     * @param function 转化器。
     * @return JSON格式的数据。
     */
    JSONObject toJson(Function<T, JSONObject> function);
}
