package org.lpw.clivia.account.type;

public interface AccountTypes {
    /**
     * 存入。
     */
    String DEPOSIT = "deposit";
    /**
     * 取出。
     */
    String WITHDRAW = "withdraw";
    /**
     * 奖励。
     */
    String REWARD = "reward";
    /**
     * 盈利。
     */
    String PROFIT = "profit";
    /**
     * 消费。
     */
    String CONSUME = "consume";
    /**
     * 汇入。
     */
    String REMIT_IN = "remit-in";
    /**
     * 汇出。
     */
    String REMIT_OUT = "remit-out";
    /**
     * 退款。
     */
    String REFUND = "refund";

    /**
     * 获取账户操作类型。
     *
     * @param name 类型名称。
     * @return 操作类型。
     */
    AccountType get(String name);
}
