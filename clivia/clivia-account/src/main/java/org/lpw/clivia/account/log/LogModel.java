package org.lpw.clivia.account.log;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(LogModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = LogModel.NAME)
@Table(name = "t_account_log")
public class LogModel extends ModelSupport {
    static final String NAME = "clivia.account.log";

    private String user; // 用户
    private String account; // 账户
    private String owner; // 所有者
    private String type; // 类型
    private String channel; // 渠道
    private long amount; // 数量
    private long balance; // 余额
    private int state; // 状态：0-待处理；1-审核通过；2-审核不通过
    private int restate; // 重置状态：0-待处理；1-审核通过；2-审核不通过
    private Timestamp start; // 开始时间
    private Timestamp end; // 结束时间
    private String json; // 扩展属性集
    private long index; // 序号

    @Jsonable
    @Column(name = "c_user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Jsonable
    @Column(name = "c_account")
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Jsonable
    @Column(name = "c_channel")
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Jsonable
    @Column(name = "c_amount")
    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
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
    @Column(name = "c_state")
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Jsonable
    @Column(name = "c_restate")
    public int getRestate() {
        return restate;
    }

    public void setRestate(int restate) {
        this.restate = restate;
    }

    @Jsonable
    @Column(name = "c_start")
    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    @Jsonable
    @Column(name = "c_end")
    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    @Column(name = "c_json")
    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    @Column(name = "c_index", updatable = false)
    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }
}
