package org.lpw.clivia.group;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(GroupModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = GroupModel.NAME)
@Table(name = "t_group")
public class GroupModel extends ModelSupport {
    static final String NAME = "clivia.group";

    private int type; // 类型：0-好友；1-群组
    private String name; // 名称
    private String avatar; // 头像
    private String notice; // 公告
    private int join; // 加入：0-正常；1-需审核；2-禁止
    private int ban; // 禁言：0-不禁言；1-全禁言；2-普通成员
    private int count; // 人数
    private Timestamp time; // 创建时间

    @Jsonable
    @Column(name = "c_type")
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
    @Column(name = "c_avatar")
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @Jsonable
    @Column(name = "c_notice")
    public String getNotice() {
        return notice;
    }

    public void setNotice(String notice) {
        this.notice = notice;
    }

    @Jsonable
    @Column(name = "c_join")
    public int getJoin() {
        return join;
    }

    public void setJoin(int join) {
        this.join = join;
    }

    @Jsonable
    @Column(name = "c_ban")
    public int getBan() {
        return ban;
    }

    public void setBan(int ban) {
        this.ban = ban;
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
    @Column(name = "c_time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}