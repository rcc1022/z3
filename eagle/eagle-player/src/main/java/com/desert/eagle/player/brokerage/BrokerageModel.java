package com.desert.eagle.player.brokerage;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Date;

@Component(BrokerageModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = BrokerageModel.NAME)
@Table(name = "t_player_brokerage")
public class BrokerageModel extends ModelSupport {
    static final String NAME = "eagle.player.brokerage";

    private String player; // 玩家
    private Date date; // 日期
    private int amount; // 金额

    @Jsonable
    @Column(name = "c_player")
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
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
    @Column(name = "c_amount")
    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}