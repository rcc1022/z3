package org.lpw.clivia.user.type;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserModel;

import java.util.Set;

/**
 * 认证类型处理器。
 *
 */
public interface Types {
    /**
     * 自有账号类型KEY。
     */
    String Self = "";
    /**
     * 短信验证码账号类型KEY。
     */
    String Sms = "sms";
    /**
     * SID账号类型KEY。
     */
    String Sid = "sid";

    /**
     * 是否支持类型KEY。
     *
     * @param key 类型KEY。
     * @return 支持则返回true，否则返回false。
     */
    boolean hasKey(String key);

    /**
     * 认证。
     *
     * @param key      类型KEY。
     * @param uid      UID。
     * @param password 密码。
     * @param grade    等级。
     * @return 认证通过则返回用户信息；否则返回null。
     */
    UserModel auth(String key, String uid, String password, String grade);

    /**
     * 获取UID。
     *
     * @param key      类型KEY。
     * @param uid      UID。
     * @param password 密码。
     * @return UID，如果获取失败则返回null。
     */
    Set<String> getUid(String key, String uid, String password);

    /**
     * 获取第三方认证手机号。
     *
     * @param key      类型KEY。
     * @param uid      UID。
     * @param password 密码。
     * @return 手机号，不存在则返回null。
     */
    String getMobile(String key, String uid, String password);

    /**
     * 获取第三方认证Email。
     *
     * @param key      类型KEY。
     * @param uid      UID。
     * @param password 密码。
     * @return Email，不存在则返回null。
     */
    String getEmail(String key, String uid, String password);

    /**
     * 获取第三方认证昵称。
     *
     * @param key      类型KEY。
     * @param uid      UID。
     * @param password 密码。
     * @return 昵称，不存在则返回null。
     */
    String getNick(String key, String uid, String password);

    /**
     * 获取第三方头像URL。
     *
     * @param key      类型KEY。
     * @param uid      UID。
     * @param password 密码。
     * @return 头像URL，不存在则返回null。
     */
    String getAvatar(String key, String uid, String password);

    /**
     * 获取来源。
     *
     * @param key      类型KEY。
     * @param uid      UID。
     * @param password 密码。
     * @return 来源。
     */
    String getFrom(String key, String uid, String password);

    /**
     * 获取第三方认证信息。
     *
     * @param key      类型KEY。
     * @param uid      UID。
     * @param password 密码。
     * @return 认证信息，不存在则返回null。
     */
    JSONObject getAuth(String key, String uid, String password);
}
