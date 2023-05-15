package com.desert.eagle.control;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(ControlModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = ControlModel.NAME)
@Table(name = "t_control")
public class ControlModel extends ModelSupport {
    static final String NAME = "eagle.control";

    private int mode; // 模式：0-单局；1-智能
    private int type; // 类型：0-极速PC；1-极速赛车
    private int win; // 赢取彩金
    private int winRate; // 赢取控率
    private int toWin; // 待赢取
    private int lose; // 发放彩金
    private int loseRate; // 发放控率
    private int toLose; // 待发放

    @Jsonable
    @Column(name = "c_mode")
    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
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
    @Column(name = "c_win")
    public int getWin() {
        return win;
    }

    public void setWin(int win) {
        this.win = win;
    }

    @Jsonable
    @Column(name = "c_win_rate")
    public int getWinRate() {
        return winRate;
    }

    public void setWinRate(int winRate) {
        this.winRate = winRate;
    }

    @Jsonable
    @Column(name = "c_to_win")
    public int getToWin() {
        return toWin;
    }

    public void setToWin(int toWin) {
        this.toWin = toWin;
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
    @Column(name = "c_lose_rate")
    public int getLoseRate() {
        return loseRate;
    }

    public void setLoseRate(int loseRate) {
        this.loseRate = loseRate;
    }

    @Jsonable
    @Column(name = "c_to_lose")
    public int getToLose() {
        return toLose;
    }

    public void setToLose(int toLose) {
        this.toLose = toLose;
    }
}