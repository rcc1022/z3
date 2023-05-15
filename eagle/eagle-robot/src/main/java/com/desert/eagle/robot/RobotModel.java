package com.desert.eagle.robot;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(RobotModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = RobotModel.NAME)
@Table(name = "t_robot")
public class RobotModel extends ModelSupport {
    static final String NAME = "eagle.robot";

    private String nick; // 昵称
    private String avatar; // 头像
    private String game; // 游戏

    @Jsonable
    @Column(name = "c_nick")
    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
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
    @Column(name = "c_game")
    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }
}