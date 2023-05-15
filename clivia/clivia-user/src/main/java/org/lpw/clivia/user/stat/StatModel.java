package org.lpw.clivia.user.stat;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import java.sql.Date;

@Component(StatModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = StatModel.NAME)
@Table(name = "t_user_stat")
public class StatModel extends ModelSupport {
    static final String NAME = "clivia.user.stat";

    private Date date; // 日期
    private int count; // 总数
    private int register; // 注册数
    private int online; // 在线数

    @Jsonable
    @Column(name = "c_date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
    @Column(name = "c_register")
    public int getRegister() {
        return register;
    }

    public void setRegister(int register) {
        this.register = register;
    }

    @Jsonable
    @Column(name = "c_online")
    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }
}
