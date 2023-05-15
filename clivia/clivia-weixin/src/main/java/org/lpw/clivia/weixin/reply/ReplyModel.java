package org.lpw.clivia.weixin.reply;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Component(ReplyModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = ReplyModel.NAME)
@Table(name = "t_weixin_reply")
public class ReplyModel extends ModelSupport {
    static final String NAME = "clivia.weixin.reply";

    private String key; // 引用KEY
    private int sort; // 顺序
    private String receiveType; // 接收类型
    private String receiveMessage; // 接收消息
    private String sendType; // 发送类型
    private String sendAppId; // 小程序APP ID
    private String sendMessage; // 发送消息
    private String sendTitle; // 发送标题
    private String sendDescription; // 发送描述
    private String sendUrl; // 发送链接
    private String sendPicurl; // 发送图片链接
    private int state; // 状态：0-待使用；1-使用中

    @Jsonable
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Jsonable
    @Column(name = "c_sort")
    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    @Jsonable
    @Column(name = "c_receive_type")
    public String getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(String receiveType) {
        this.receiveType = receiveType;
    }

    @Jsonable
    @Column(name = "c_receive_message")
    public String getReceiveMessage() {
        return receiveMessage;
    }

    public void setReceiveMessage(String receiveMessage) {
        this.receiveMessage = receiveMessage;
    }

    @Jsonable
    @Column(name = "c_send_type")
    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    @Jsonable
    @Column(name = "c_send_app_id")
    public String getSendAppId() {
        return sendAppId;
    }

    public void setSendAppId(String sendAppId) {
        this.sendAppId = sendAppId;
    }

    @Jsonable
    @Column(name = "c_send_message")
    public String getSendMessage() {
        return sendMessage;
    }

    public void setSendMessage(String sendMessage) {
        this.sendMessage = sendMessage;
    }

    @Jsonable
    @Column(name = "c_send_title")
    public String getSendTitle() {
        return sendTitle;
    }

    public void setSendTitle(String sendTitle) {
        this.sendTitle = sendTitle;
    }

    @Jsonable
    @Column(name = "c_send_description")
    public String getSendDescription() {
        return sendDescription;
    }

    public void setSendDescription(String sendDescription) {
        this.sendDescription = sendDescription;
    }

    @Jsonable
    @Column(name = "c_send_url")
    public String getSendUrl() {
        return sendUrl;
    }

    public void setSendUrl(String sendUrl) {
        this.sendUrl = sendUrl;
    }

    @Jsonable
    @Column(name = "c_send_picurl")
    public String getSendPicurl() {
        return sendPicurl;
    }

    public void setSendPicurl(String sendPicurl) {
        this.sendPicurl = sendPicurl;
    }

    @Jsonable
    @Column(name = "c_state")
    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
