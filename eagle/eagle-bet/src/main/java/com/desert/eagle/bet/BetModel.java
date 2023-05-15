package com.desert.eagle.bet;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(BetModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = BetModel.NAME)
@Table(name = "t_bet")
public class BetModel extends ModelSupport {
    static final String NAME = "eagle.bet";

    private String game; // 游戏
    private String player; // 玩家
    private String issue; // 期号
    private String type; // 类型
    private String item; // 项目
    private String subitem; // 子项目
    private int rate; // 赔率
    private int amount; // 金额
    private int water; // 返水
    private int commission; // 上级佣金
    private int profit; // 输赢
    private int stop; // 追号停止
    private String zhuihao; // 追号编号
    private String memo; // 备注
    private String open; // 开奖结果
    private int status; // 状态：0-未结算；1-已结算
    private int robot; // 机器人：0-否；1-是
    private Timestamp time; // 时间
    private Timestamp settle; // 结算时间

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
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    @Jsonable
    @Column(name = "c_issue")
    public String getIssue() {
        return issue;
    }

    public void setIssue(String issue) {
        this.issue = issue;
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
    @Column(name = "c_item")
    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    @Jsonable
    @Column(name = "c_subitem")
    public String getSubitem() {
        return subitem;
    }

    public void setSubitem(String subitem) {
        this.subitem = subitem;
    }

    @Jsonable
    @Column(name = "c_rate")
    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
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
    @Column(name = "c_commission")
    public int getCommission() {
        return commission;
    }

    public void setCommission(int commission) {
        this.commission = commission;
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
    @Column(name = "c_stop")
    public int getStop() {
        return stop;
    }

    public void setStop(int stop) {
        this.stop = stop;
    }

    @Jsonable
    @Column(name = "c_zhuihao")
    public String getZhuihao() {
        return zhuihao;
    }

    public void setZhuihao(String zhuihao) {
        this.zhuihao = zhuihao;
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
    @Column(name = "c_open")
    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    @Jsonable
    @Column(name = "c_status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Jsonable
    @Column(name = "c_robot")
    public int getRobot() {
        return robot;
    }

    public void setRobot(int robot) {
        this.robot = robot;
    }

    @Jsonable
    @Column(name = "c_time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Jsonable
    @Column(name = "c_settle")
    public Timestamp getSettle() {
        return settle;
    }

    public void setSettle(Timestamp settle) {
        this.settle = settle;
    }
}