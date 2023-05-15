package org.lpw.clivia.popular;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface PopularService {
    /**
     * 是否存在验证器Bean名称。
     */
    String VALIDATOR_EXISTS = PopularModel.NAME + ".validator.exists";

    /**
     * 检索。
     *
     * @param key   引用key。
     * @param state 状态：-1-全部；0-正常；1-禁用。
     * @return 数据集。
     */
    JSONObject query(String key, int state);

    /**
     * 发布。
     *
     * @param key  引用key。
     * @param size 数据量。
     * @return 数据集。
     */
    JSONArray publish(String key, int size);

    /**
     * 增加。
     *
     * @param key   引用key。
     * @param value 值。
     */
    void increase(String key, String value);

    /**
     * 增加。
     *
     * @param key    引用key。
     * @param values 值集。
     */
    void increase(String key, String[] values);

    /**
     * 修改状态。
     *
     * @param id    ID值。
     * @param state 状态：0-正常；1-禁用。
     */
    void state(String id, int state);
}
