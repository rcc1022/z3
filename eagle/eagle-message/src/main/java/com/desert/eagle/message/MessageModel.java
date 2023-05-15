package com.desert.eagle.message;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(MessageModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = MessageModel.NAME)
@Table(name = "t_message")
public class MessageModel extends ModelSupport {
    static final String NAME = "eagle.message";

    private String game; // 游戏
    private String player; // 玩家
    private int type; // 类型：0-开盘；1-封盘；2-下注；3-下注列表
    private String content; // 内容
    private long time; // 时间

    @Jsonable
    @Column(name = "c_game")
    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

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
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

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
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}