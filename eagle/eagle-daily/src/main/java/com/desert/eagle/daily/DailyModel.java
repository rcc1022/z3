package com.desert.eagle.daily;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Component(DailyModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = DailyModel.NAME)
@Table(name = "t_daily")
public class DailyModel extends ModelSupport {
    static final String NAME = "eagle.daily";

    private Date date; // 日期
    private String game; // 游戏
    private int player; // 玩家数
    private int count; // 下注笔数
    private int bet; // 下注额
    private int profit; // 输赢额
    private int water; // 已返水
    private int water0; // 待返水
    private int commission; // 佣金
    private int gain; // 站点赢利
    private int deposit; // 充值
    private int gift; // 补单充值
    private int withdraw; // 提现
    private int register; // 注册人数

    @Jsonable
    @Column(name = "c_date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Jsonable
    @Column(name = "c_game")
    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    @Jsonable
    @Column(name = "c_player")
    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    @Jsonable
    @Column(name = "c_count")
    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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
    @Column(name = "c_water")
    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    @Jsonable
    @Column(name = "c_water0")
    public int getWater0() {
        return water0;
    }

    public void setWater0(int water0) {
        this.water0 = water0;
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
    @Column(name = "c_gain")
    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    @Jsonable
    @Column(name = "c_deposit")
    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

    @Jsonable
    @Column(name = "c_gift")
    public int getGift() {
        return gift;
    }

    public void setGift(int gift) {
        this.gift = gift;
    }

    @Jsonable
    @Column(name = "c_withdraw")
    public int getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(int withdraw) {
        this.withdraw = withdraw;
    }

    @Jsonable
    @Column(name = "c_register")
    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }
}