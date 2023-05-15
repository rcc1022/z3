package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;

public interface MetaDeclare {
    /**
     * 获取uri。
     * 
     * @return uri。
     */
    String getUri();

    /**
     * 获取属性集。
     * 
     * @param service 服务。
     * @return 属性集。
     */
    JSONArray getProps(String service);
}
