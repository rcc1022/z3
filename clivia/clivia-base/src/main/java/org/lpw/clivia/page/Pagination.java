package org.lpw.clivia.page;

/**
 * 分页。
 *
 */
public interface Pagination {
    /**
     * 获取每页显示记录数。
     *
     * @return 每页显示记录数。
     */
    int getPageSize();

    /**
     * 获取每页显示记录数。如果未提供或小于等于0则使用默认值。
     *
     * @param defaultSize 默认值。
     * @return 每页显示记录数。
     */
    int getPageSize(int defaultSize);

    /**
     * 获取当前显示页数。
     *
     * @return 当前显示页数。
     */
    int getPageNum();
}
