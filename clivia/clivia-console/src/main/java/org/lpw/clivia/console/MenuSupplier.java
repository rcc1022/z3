package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;

public interface MenuSupplier {
    /**
     * 引用KEY。
     *
     * @return 引用KEY。
     */
    String key();

    /**
     * 获取菜单项集合。
     *
     * @return 菜单项集合。
     */
    JSONArray items();
}
