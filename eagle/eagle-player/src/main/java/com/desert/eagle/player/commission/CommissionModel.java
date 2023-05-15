package com.desert.eagle.player.commission;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(CommissionModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = CommissionModel.NAME)
@Table(name = "t_player_commission")
public class CommissionModel extends ModelSupport {
    static final String NAME = "eagle.player.commission";

    private String player; // 玩家
    private int amount; // 金额
    private int status; // 状态：0-待审核；1-已通过；2-已拒绝
    private Timestamp submit; // 提交时间
    private Timestamp audit; // 审核时间

    @Jsonable
    @Column(name = "c_player")
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
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
    @Column(name = "c_status")
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Jsonable
    @Column(name = "c_submit")
    public Timestamp getSubmit() {
        return submit;
    }

    public void setSubmit(Timestamp submit) {
        this.submit = submit;
    }

    @Jsonable
    @Column(name = "c_audit")
    public Timestamp getAudit() {
        return audit;
    }

    public void setAudit(Timestamp audit) {
        this.audit = audit;
    }
}