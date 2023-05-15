package com.desert.eagle.scnum;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(ScnumModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = ScnumModel.NAME)
@Table(name = "t_scnum")
public class ScnumModel extends ModelSupport {
    static final String NAME = "eagle.scnum";

    private int type; // 类型：0-幸运飞艇；1-极速赛车·私；2-澳洲10；3-极速赛车
    private long issue; // 期号
    private int num1; // 号码1
    private int num2; // 号码2
    private int num3; // 号码3
    private int num4; // 号码4
    private int num5; // 号码5
    private int num6; // 号码6
    private int num7; // 号码7
    private int num8; // 号码8
    private int num9; // 号码9
    private int num10; // 号码10
    private int sum; // 冠亚和
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
    @Column(name = "c_num6")
    public int getNum6() {
        return num6;
    }

    public void setNum6(int num6) {
        this.num6 = num6;
    }

    @Jsonable
    @Column(name = "c_num7")
    public int getNum7() {
        return num7;
    }

    public void setNum7(int num7) {
        this.num7 = num7;
    }

    @Jsonable
    @Column(name = "c_num8")
    public int getNum8() {
        return num8;
    }

    public void setNum8(int num8) {
        this.num8 = num8;
    }

    @Jsonable
    @Column(name = "c_num9")
    public int getNum9() {
        return num9;
    }

    public void setNum9(int num9) {
        this.num9 = num9;
    }

    @Jsonable
    @Column(name = "c_num10")
    public int getNum10() {
        return num10;
    }

    public void setNum10(int num10) {
        this.num10 = num10;
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