package org.lpw.clivia.api;

import com.alibaba.fastjson.JSONArray;

public interface ApiService {
    /**
     * 获取API配置集。
     *
     * @return API配置集。
     */
    JSONArray get();
}
