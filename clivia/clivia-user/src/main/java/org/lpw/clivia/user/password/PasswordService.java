package org.lpw.clivia.user.password;

import com.alibaba.fastjson.JSONObject;

public interface PasswordService {
    /**
     * 获取当前用户设置的密码项。
     *
     * @return 当前用户设置的密码项。
     */
    JSONObject has();

    /**
     * 验证密码。
     *
     * @param user     用户，为空则为当前用户。
     * @param type     类型。
     * @param password 密码。
     * @return 验证通过则返回true；否则返回false。
     */
    boolean auth(String user, String type, String password);

    /**
     * 设置密码。
     *
     * @param user     用户，为空则为当前用户。
     * @param type     类型。
     * @param password 密码。
     * @param old      旧密码。
     * @param unique   是否唯一，为true则不允许与其他密码重复。
     * @return 设置结果。
     */
    String set(String user, String type, String password, String old, boolean unique);

    /**
     * 关闭密码。
     *
     * @param user     用户，为空则为当前用户。
     * @param type     类型。
     * @param password 密码。
     * @return 关闭成功则返回true；否则返回false。
     */
    boolean off(String user, String type, String password);

    /**
     * 注销用户。
     *
     * @param user     用户。
     * @param password 密码。
     */
    void destroy(String user, String password);
}
