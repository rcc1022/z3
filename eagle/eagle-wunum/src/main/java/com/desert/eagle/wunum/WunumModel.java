package com.desert.eagle.wunum;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(WunumModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = WunumModel.NAME)
@Table(name = "t_wunum")
public class WunumModel extends ModelSupport {
    static final String NAME = "eagle.wunum";

    private int type; // 类型：0-澳洲幸运5；极速时时彩
    private long issue; // 期号
    private int num1; // 号码1
    private int num2; // 号码2
    private int num3; // 号码3
    private int num4; // 号码4
    private int num5; // 号码5
    private int sum; // 总和
    private int status; // 状态：0-预设；1-已开奖
    private Timestamp time; // 时间

    @Jsonable
    @Column(name = "c_type")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Jsonable
    @Column(name = "c_issue")
    public long getIssue() {
        return issue;
    }

    public void setIssue(long issue) {
        this.issue = issue;
    }

    @Jsonable
    @Column(name = "c_num1")
    public int getNum1() {
        return num1;
    }

    public void setNum1(int num1) {
        this.num1 = num1;
    }

    @Jsonable
    @Column(name = "c_num2")
    public int getNum2() {
        return num2;
    }

    public void setNum2(int num2) {
        this.num2 = num2;
    }

    @Jsonable
    @Column(name = "c_num3")
    public int getNum3() {
        return num3;
    }

    public void setNum3(int num3) {
        this.num3 = num3;
    }

    @Jsonable
    @Column(name = "c_num4")
    public int getNum4() {
        return num4;
    }

    public void setNum4(int num4) {
        this.num4 = num4;
    }

    @Jsonable
    @Column(name = "c_num5")
    public int getNum5() {
        return num5;
    }

    public void setNum5(int num5) {
        this.num5 = num5;
    }

    @Jsonable
    @Column(name = "c_sum")
    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
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
    @Column(name = "c_time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}