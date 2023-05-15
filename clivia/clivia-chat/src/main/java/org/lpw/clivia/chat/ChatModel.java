package org.lpw.clivia.chat;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.Memory;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(ChatModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = ChatModel.NAME)
@Table(name = "t_chat")
@Memory(name = "m_chat")
public class ChatModel extends ModelSupport {
    static final String NAME = "clivia.chat";

    private String group; // 群组
    private String sender; // 发送人
    private String genre; // 类型
    private String body; // 内容
    private long time; // 时间

    @Jsonable
    @Column(name = "c_group")
    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    @Jsonable
    @Column(name = "c_sender")
    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    @Jsonable
    @Column(name = "c_genre")
    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    @Jsonable
    @Column(name = "c_body")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Jsonable
    @Column(name = "c_time")
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}