package com.desert.eagle.game;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(GameModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = GameModel.NAME)
@Table(name = "t_game")
public class GameModel extends ModelSupport {
    static final String NAME = "eagle.game";

    private int sort; // 顺序
    private String name; // 名称
    private String cover; // 封面图
    private int type; // 类型：0-加拿大PC2.0；1-加拿大PC2.8；2-加拿大PC3.2；3-极速PC·私2.0；4-极速PC·私2.8；5-极速PC·私3.2；6-幸运飞艇；7-极速赛车·私；8-澳洲10；9-极速赛车；10-澳洲幸运5；11-皇冠足球
    private int water; // 返水比例
    private int lose; // 亏损返水
    private int commission; // 上级佣金
    private int min; // 最小投注额
    private int max; // 最大投注额
    private int total; // 当期最大投注总额
    private int close; // 封盘时间：秒
    private int control; // 控奖：0-否；1-是
    private int rate; // 控率
    private int robot; // 机器人数
    private int on; // 开启：0-关；1-开
    private String rule; // 玩法规则

    @Jsonable
    @Column(name = "c_sort")
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Jsonable
    @Column(name = "c_name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Jsonable
    @Column(name = "c_cover")
    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
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
    @Column(name = "c_water")
    public int getWater() {
        return water;
    }

    public void setWater(int water) {
        this.water = water;
    }

    @Jsonable
    @Column(name = "c_lose")
    public int getLose() {
        return lose;
    }

    public void setLose(int lose) {
        this.lose = lose;
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
    @Column(name = "c_min")
    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    @Jsonable
    @Column(name = "c_max")
    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Jsonable
    @Column(name = "c_total")
    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    @Jsonable
    @Column(name = "c_close")
    public int getClose() {
        return close;
    }

    public void setClose(int close) {
        this.close = close;
    }

    @Jsonable
    @Column(name = "c_control")
    public int getControl() {
        return control;
    }

    public void setControl(int control) {
        this.control = control;
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
    @Column(name = "c_robot")
    public int getRobot() {
        return robot;
    }

    public void setRobot(int robot) {
        this.robot = robot;
    }

    @Jsonable
    @Column(name = "c_on")
    public int getOn() {
        return on;
    }

    public void setOn(int on) {
        this.on = on;
    }

    @Jsonable
    @Column(name = "c_rule")
    public String getRule() {
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }
}