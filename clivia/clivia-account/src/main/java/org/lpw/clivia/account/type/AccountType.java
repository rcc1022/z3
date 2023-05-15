package org.lpw.clivia.account.type;

import org.lpw.clivia.account.AccountModel;
import org.lpw.clivia.account.log.LogModel;

import java.util.Map;

public interface AccountType {
    /**
     * 获取类型名称。
     *
     * @return 类型名称。
     */
    String getName();

    /**
     * 账户变动。
     *
     * @param account 账户。
     * @param channel 渠道。
     * @param amount  数量。
     * @param map     参数集。
     * @return 日志ID值。
     */
    String change(AccountModel account, String channel, long amount, Map<String, String> map);

    /**
     * 结算。
     *
     * @param account 账户。
     * @param log     操作日志。
     * @return 如果结算成功则返回true；否则返回false。
     */
    boolean complete(AccountModel account, LogModel log);
}
