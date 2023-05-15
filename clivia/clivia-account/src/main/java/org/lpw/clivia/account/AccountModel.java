package org.lpw.clivia.account;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(AccountModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = AccountModel.NAME)
@Table(name = "t_account")
public class AccountModel extends ModelSupport {
    static final String NAME = "clivia.account";

    private String user; // 用户
    private String owner; // 所有者
    private int type; // 类型
    private long balance; // 余额
    private long deposit; // 存入总额
    private long withdraw; // 取出总额
    private long reward; // 奖励总额
    private long profit; // 盈利总额
    private long consume; // 消费总额
    private long remitIn; // 汇入总额
    private long remitOut; // 汇出总额
    private long refund; // 退款总额
    private long pending; // 待结算总额
    private String checksum; // 校验值
    private String lockId; // 全局锁ID

    @Jsonable
    @Column(name = "c_user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Jsonable
    @Column(name = "c_owner")
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Jsonable
    @Column(name = "c_type")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Jsonable
    @Column(name = "c_balance")
    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    @Jsonable
    @Column(name = "c_deposit")
    public long getDeposit() {
        return deposit;
    }

    public void setDeposit(long deposit) {
        this.deposit = deposit;
    }

    @Jsonable
    @Column(name = "c_withdraw")
    public long getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(long withdraw) {
        this.withdraw = withdraw;
    }

    @Jsonable
    @Column(name = "c_reward")
    public long getReward() {
        return reward;
    }

    public void setReward(long reward) {
        this.reward = reward;
    }

    @Jsonable
    @Column(name = "c_profit")
    public long getProfit() {
        return profit;
    }

    public void setProfit(long profit) {
        this.profit = profit;
    }

    @Jsonable
    @Column(name = "c_consume")
    public long getConsume() {
        return consume;
    }

    public void setConsume(long consume) {
        this.consume = consume;
    }

    @Jsonable
    @Column(name = "c_remit_in")
    public long getRemitIn() {
        return remitIn;
    }

    public void setRemitIn(long remitIn) {
        this.remitIn = remitIn;
    }

    @Jsonable
    @Column(name = "c_remit_out")
    public long getRemitOut() {
        return remitOut;
    }

    public void setRemitOut(long remitOut) {
        this.remitOut = remitOut;
    }

    @Jsonable
    @Column(name = "c_refund")
    public long getRefund() {
        return refund;
    }

    public void setRefund(long refund) {
        this.refund = refund;
    }

    @Jsonable
    @Column(name = "c_pending")
    public long getPending() {
        return pending;
    }

    public void setPending(long pending) {
        this.pending = pending;
    }

    @Column(name = "c_checksum")
    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getLockId() {
        return lockId;
    }

    public void setLockId(String lockId) {
        this.lockId = lockId;
    }
}
