package com.desert.eagle.player;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(PlayerModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = PlayerModel.NAME)
@Table(name = "t_player")
public class PlayerModel extends ModelSupport {
    static final String NAME = "eagle.player";

    private String uid; // UID
    private volatile int balance; // 余额
    private int bet; // 下注流水
    private int profit; // 输赢
    private String invitor; // 推荐人
    private String qrcode; // 二维码
    private String qruri; // 二维码图片URI
    private String memo; // 备注
    private int commission; // 总佣金
    private int commissionBalance; // 佣金余额
    private int commissionGenerate; // 产生佣金
    private Timestamp login; // 最后一次登录时间
    private String ip; // IP
    private int ban; // 封禁：0-否；1-是
    private int junior; // 下级人数
    private Timestamp time;

    @Jsonable
    @Column(name = "c_uid")
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Jsonable
    @Column(name = "c_balance")
    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Jsonable
    @Column(name = "c_bet")
    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    @Jsonable
    @Column(name = "c_profit")
    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    @Jsonable
    @Column(name = "c_invitor")
    public String getInvitor() {
        return invitor;
    }

    public void setInvitor(String invitor) {
        this.invitor = invitor;
    }

    @Jsonable
    @Column(name = "c_qrcode")
    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    @Jsonable
    @Column(name = "c_qruri")
    public String getQruri() {
        return qruri;
    }

    public void setQruri(String qruri) {
        this.qruri = qruri;
    }

    @Jsonable
    @Column(name = "c_memo")
    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Jsonable
    @Column(name = "c_commission")
    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
        this.commission = commission;
    }

    @Jsonable
    @Column(name = "c_commission_balance")
    public int getCommissionBalance() {
        return commissionBalance;
    }

    public void setCommissionBalance(int commissionBalance) {
        this.commissionBalance = commissionBalance;
    }

    @Jsonable
    @Column(name = "c_commission_generate")
    public int getCommissionGenerate() {
        return commissionGenerate;
    }

    public void setCommissionGenerate(int commissionGenerate) {
        this.commissionGenerate = commissionGenerate;
    }

    @Jsonable
    @Column(name = "c_login")
    public Timestamp getLogin() {
        return login;
    }

    public void setLogin(Timestamp login) {
        this.login = login;
    }

    @Jsonable
    @Column(name = "c_ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Jsonable
    @Column(name = "c_ban")
    public int getBan() {
        return ban;
    }

    public void setBan(int ban) {
        this.ban = ban;
    }

    @Jsonable
    @Column(name = "c_junior")
    public int getJunior() {
        return junior;
    }

    public void setJunior(int junior) {
        this.junior = junior;
    }

    @Jsonable
    @Column(name = "c_time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}