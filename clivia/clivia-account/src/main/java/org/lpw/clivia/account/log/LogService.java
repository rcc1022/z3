package org.lpw.clivia.account.log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.account.AccountModel;

import java.util.Map;

public interface LogService {
    /**
     * 状态。
     */
    enum State {
        /**
         * 待处理。
         */
        New,
        /**
         * 审核通过。
         */
        Pass,
        /**
         * 审核不通过。
         */
        Reject
    }

    /**
     * 检索账户操作日志集。
     *
     * @param uid     用户UID。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param state   状态：0-待处理；1-审核通过；2-审核不通过；3-已完成。
     * @param start   开始时间范围。
     * @return 操作日志集。
     */
    JSONObject query(String uid, String owner, String type, String channel, int state, String start);

    /**
     * 新增日志。
     *
     * @param account 账户信息。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @param state   状态。
     * @param map     参数集。
     * @return 日志ID值。
     */
    String create(AccountModel account, String type, String channel, long amount, State state, Map<String, String> map);

    /**
     * 设置审核通过。
     *
     * @param ids ID集。
     * @return 已完成的日志
     */
    JSONArray pass(String[] ids);

    /**
     * 设置审核不通过。
     *
     * @param ids ID集。
     */
    JSONArray reject(String[] ids);
}
