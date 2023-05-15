package com.desert.eagle.player.deposit;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(DepositModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = DepositModel.NAME)
@Table(name = "t_player_deposit")
public class DepositModel extends ModelSupport {
    static final String NAME = "eagle.player.deposit";

    private String player; // 玩家
    private String type; // 类型
    private int amount; // 金额
    private int status; // 状态：0-待审核；1-已通过；2-已拒绝
    private int check; // 审核：0-否；1-是
    private Timestamp submit; // 提交时间
    private Timestamp audit; // 审核时间
    private String auditor; // 审核/操作人

    @Jsonable
    @Column(name = "c_player")
    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
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
    @Column(name = "c_check")
    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
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

    @Jsonable
    @Column(name = "c_auditor")
    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }
}