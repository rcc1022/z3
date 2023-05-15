package org.lpw.clivia.account;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.account.log.LogModel;

import java.util.Map;

public interface AccountService {
    /**
     * 结算状态。
     */
    enum Complete {
        /**
         * 成功。
         */
        Success,
        /**
         * 锁定。
         */
        Locked,
        /**
         * 失败。
         */
        Failure
    }

    /**
     * 检索账户信息集。
     *
     * @param uid     用户ID或UID，为空表示全部。
     * @param owner   所有者ID，null或all表示所有。
     * @param type    类型，-1表示全部。
     * @param balance 余额范围。
     * @return 账户信息集。
     */
    JSONObject query(String uid, String owner, int type, String balance);

    /**
     * 检索用户账户信息集。
     *
     * @param user  用户ID，为空则为当前用户。
     * @param owner 所有者ID，null则表示所有。
     * @param fill  是否填充关联数据。
     * @return 账户信息集。
     */
    JSONArray queryUser(String user, String owner, boolean fill);

    /**
     * 检索账户信息并合并。
     *
     * @param user  用户ID，为空则为当前用户。
     * @param owner 所有者ID，null则表示所有。
     * @param fill  是否填充关联数据。
     * @return 账户信息。
     */
    JSONObject merge(String user, String owner, boolean fill);

    /**
     * 存入。
     *
     * @param user    用户。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @param map     参数集。
     * @return 账户信息；如果存入失败则返回null。
     */
    JSONObject deposit(String user, String owner, int type, String channel, long amount, Map<String, String> map);

    /**
     * 取出。
     *
     * @param user    用户。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @param map     参数集。
     * @return 账户信息；如果取出失败则返回null。
     */
    JSONObject withdraw(String user, String owner, int type, String channel, long amount, Map<String, String> map);

    /**
     * 奖励。
     *
     * @param user    用户。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @return 账户信息；如果奖励失败则返回null。
     */
    JSONObject reward(String user, String owner, int type, String channel, long amount);

    /**
     * 盈利。
     *
     * @param user    用户。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @return 账户信息；如果盈利失败则返回null。
     */
    JSONObject profit(String user, String owner, int type, String channel, long amount);

    /**
     * 消费。
     *
     * @param user    用户。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @return 账户信息；如果消费失败则返回null。
     */
    JSONObject consume(String user, String owner, int type, String channel, long amount);

    /**
     * 汇入。
     *
     * @param user    用户。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @return 账户信息；如果消费失败则返回null。
     */
    JSONObject remitIn(String user, String owner, int type, String channel, long amount);

    /**
     * 汇出。
     *
     * @param user    用户。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @return 账户信息；如果消费失败则返回null。
     */
    JSONObject remitOut(String user, String owner, int type, String channel, long amount);

    /**
     * 退款。
     *
     * @param user    用户。
     * @param owner   所有者。
     * @param type    类型。
     * @param channel 渠道。
     * @param amount  数量。
     * @return 账户信息；如果消费失败则返回null。
     */
    JSONObject refund(String user, String owner, int type, String channel, long amount);

    /**
     * 结算。
     *
     * @param log 操作日志。
     * @return 结算状态。
     */
    Complete complete(LogModel log);
}
