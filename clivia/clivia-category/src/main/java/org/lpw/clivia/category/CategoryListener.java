package org.lpw.clivia.category;

import java.util.Set;

/**
 * 分类监听器。
 *
 */
public interface CategoryListener {
    /**
     * 分类信息变更。
     *
     * @param id       ID。
     * @param parents  父节点集。
     * @param children 子节点集。
     */
    void categoryChanged(String id, Set<String> parents, Set<String> children);
}
