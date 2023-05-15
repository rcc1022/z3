package org.lpw.clivia.ad;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface AdService {
    /**
     * 存在验证器Bean名称。
     */
    String VALIDATOR_EXISTS = AdModel.NAME + ".validator.exists";

    /**
     * 检索。
     *
     * @param type  类型。
     * @param state 状态：-1-全部；0-下线；1-上线。
     * @return 广告集。
     */
    JSONObject query(String type, int state);

    /**
     * 检索发布集。
     *
     * @param type 类型。
     * @return 广告集。
     */
    JSONArray publish(String type);

    /**
     * 保存。
     *
     * @param ad 广告。
     */
    void save(AdModel ad);

    /**
     * 删除。
     *
     * @param id ID值。
     */
    void delete(String id);
}
