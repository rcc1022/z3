package org.lpw.clivia.user.online;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.Memory;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(OnlineModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = OnlineModel.NAME)
@Table(name = "t_user_online")
@Memory(name = "m_user_online")
public class OnlineModel extends ModelSupport {
    static final String NAME = "clivia.user.online";

    private String user; // 用户
    private int grade; // 等级
    private String ip; // IP地址
    private String sid; // Session ID
    private Timestamp signIn; // 登入时间
    private Timestamp lastVisit; // 最后访问时间

    @Jsonable
    @Column(name = "c_user")
    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @Jsonable
    @Column(name = "c_grade")
    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    @Jsonable
    @Column(name = "c_ip")
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Jsonable
    @Column(name = "c_sid")
    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    @Jsonable
    @Column(name = "c_sign_in")
    public Timestamp getSignIn() {
        return signIn;
    }

    public void setSignIn(Timestamp signIn) {
        this.signIn = signIn;
    }

    @Jsonable
    @Column(name = "c_last_visit")
    public Timestamp getLastVisit() {
        return lastVisit;
    }

    public void setLastVisit(Timestamp lastVisit) {
        this.lastVisit = lastVisit;
    }
}
