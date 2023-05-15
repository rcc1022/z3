package org.lpw.clivia.weixin;

import org.lpw.photon.dao.model.Jsonable;
import org.lpw.photon.dao.model.ModelSupport;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Component(WeixinModel.NAME + ".model")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Entity(name = WeixinModel.NAME)
@Table(name = "t_weixin")
public class WeixinModel extends ModelSupport {
    static final String NAME = "clivia.weixin";

    private String key; // 引用key
    private String name; // 名称
    private String appId; // APP ID
    private String secret; // 密钥
    private String token; // 验证Token
    private String mchId; // 商户ID
    private String mchPartnerId; // 合作方ID
    private String mchKey; // 商户密钥
    private String mchSerialNo; // 商户证书序列号
    private String mchPrivateKey; // 商户私钥
    private String mchKeyV3; // 商户密钥V3
    private String accessToken; // Access Token
    private String jsapiTicket; // Jsapi Ticket
    private String menu; // 菜单配置
    private Timestamp time; // 更新时间

    @Jsonable
    @Column(name = "c_key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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
    @Column(name = "c_app_id")
    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    @Jsonable
    @Column(name = "c_secret")
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Jsonable
    @Column(name = "c_token")
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Jsonable
    @Column(name = "c_mch_id")
    public String getMchId() {
        return mchId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    @Jsonable
    @Column(name = "c_mch_partner_id")
    public String getMchPartnerId() {
        return mchPartnerId;
    }

    public void setMchPartnerId(String mchPartnerId) {
        this.mchPartnerId = mchPartnerId;
    }

    @Jsonable
    @Column(name = "c_mch_key")
    public String getMchKey() {
        return mchKey;
    }

    public void setMchKey(String mchKey) {
        this.mchKey = mchKey;
    }

    @Jsonable
    @Column(name = "c_mch_serial_no")
    public String getMchSerialNo() {
        return mchSerialNo;
    }

    public void setMchSerialNo(String mchSerialNo) {
        this.mchSerialNo = mchSerialNo;
    }

    @Jsonable
    @Column(name = "c_mch_private_key")
    public String getMchPrivateKey() {
        return mchPrivateKey;
    }

    public void setMchPrivateKey(String mchPrivateKey) {
        this.mchPrivateKey = mchPrivateKey;
    }

    @Jsonable
    @Column(name = "c_mch_key_v3")
    public String getMchKeyV3() {
        return mchKeyV3;
    }

    public void setMchKeyV3(String mchKeyV3) {
        this.mchKeyV3 = mchKeyV3;
    }

    @Jsonable
    @Column(name = "c_access_token")
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Jsonable
    @Column(name = "c_jsapi_ticket")
    public String getJsapiTicket() {
        return jsapiTicket;
    }

    public void setJsapiTicket(String jsapiTicket) {
        this.jsapiTicket = jsapiTicket;
    }

    @Jsonable
    @Column(name = "c_menu")
    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
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
