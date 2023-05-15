package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface MetaHelper {
    /**
     * 获取元数据。
     *
     * @param key 模块KEY。
     * @param all 所有，不控制权限。
     * @return 元数据；如果不存在则返回null。
     */
    JSONObject get(String key, boolean all);

    /**
     * 合并属性。
     *
     * @param meta 元数据。
     * @param m    子配置。
     * @return 合并后的props属性。
     */
    JSONArray props(JSONObject meta, JSONObject m);
}
