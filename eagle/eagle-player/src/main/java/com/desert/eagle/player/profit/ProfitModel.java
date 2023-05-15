package com.desert.eagle.player.profit;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Component(ProfitModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = ProfitModel.NAME)
@Table(name = "t_player_profit")
public class ProfitModel extends ModelSupport {
    static final String NAME = "eagle.player.profit";

    private String player; // 玩家
    private String game; // 游戏
    private Date date; // 日期
    private int deposit; // 充值
    private int withdraw; // 提现
    private int count; // 下注笔数
    private int amount; // 下注总额
    private int water; // 返水
    private int water0; // 待返水
    private int water2; // 拒绝返水
    private int waterLose; // 亏损返水
    private int profit; // 输赢
    private int commission; // 佣金
    private int status; // 状态：0-待结算；1-已通过；2-已拒绝

    @Jsonable
    @Column(name = "c_player")
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
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
    @Column(name = "c_date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
    @Column(name = "c_withdraw")
    public int getWithdraw() {
        return withdraw;
    }

    public void setWithdraw(int withdraw) {
        this.withdraw = withdraw;
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
    @Column(name = "c_amount")
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
    @Column(name = "c_water2")
    public int getWater2() {
        return water2;
    }

    public void setWater2(int water2) {
        this.water2 = water2;
    }

    @Jsonable
    @Column(name = "c_water_lose")
    public int getWaterLose() {
        return waterLose;
    }

    public void setWaterLose(int waterLose) {
        this.waterLose = waterLose;
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
    @Column(name = "c_commission")
    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
        this.commission = commission;
    }

    @Jsonable
    @Column(name = "c_status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}