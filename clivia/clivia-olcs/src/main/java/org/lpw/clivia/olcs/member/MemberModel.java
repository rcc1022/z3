package org.lpw.clivia.olcs.member;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(MemberModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = MemberModel.NAME)
@Table(name = "t_olcs_member")
public class MemberModel extends ModelSupport {
    static final String NAME = "clivia.olcs.member";

    private String content; // 内容
    private Timestamp time; // 消息时间
    private int userUnread; // 用户未读数
    private int replierUnread; // 客服未读数
    private Timestamp userRead; // 用户阅读时间
    private Timestamp replierRead; // 客服阅读时间


    @Jsonable
    @Column(name = "c_content")
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Jsonable
    @Column(name = "c_time")
    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @Jsonable
    @Column(name = "c_user_unread")
    public int getUserUnread() {
        return userUnread;
    }

    public void setUserUnread(int userUnread) {
        this.userUnread = userUnread;
    }

    @Jsonable
    @Column(name = "c_replier_unread")
    public int getReplierUnread() {
        return replierUnread;
    }

    public void setReplierUnread(int replierUnread) {
        this.replierUnread = replierUnread;
    }

    @Jsonable
    @Column(name = "c_user_read")
    public Timestamp getUserRead() {
        return userRead;
    }

    public void setUserRead(Timestamp userRead) {
        this.userRead = userRead;
    }

    @Jsonable
    @Column(name = "c_replier_read")
    public Timestamp getReplierRead() {
        return replierRead;
    }

    public void setReplierRead(Timestamp replierRead) {
        this.replierRead = replierRead;
    }
}