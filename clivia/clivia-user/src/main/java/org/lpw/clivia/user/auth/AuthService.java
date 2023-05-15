package org.lpw.clivia.user.auth;

import com.alibaba.fastjson.JSONArray;

import java.util.List;
import java.util.Set;

public interface AuthService {
    /**
     * UID不存在验证器Bean名称。
     * 默认错误信息key=clivia.user.auth.uid.exists。
     */
    String VALIDATOR_UID_NOT_EXISTS = AuthModel.NAME + ".validator.uid.not-exists";
    /**
     * UID存在验证器Bean名称。
     * 默认错误信息key=clivia.user.auth.uid.not-exists。
     */
    String VALIDATOR_UID_EXISTS = AuthModel.NAME + ".validator.uid.exists";

    /**
     * 检索用户的认证信息集。
     *
     * @param user 用户ID。
     * @return 认证信息集，如果不存在则返回空JSON数组。
     */
    JSONArray query(String user);

    /**
     * 检索用户的认证信息集。
     *
     * @param user 用户ID。
     * @return 认证信息集，如果不存在则返回空集。
     */
    List<AuthModel> list(String user);

    /**
     * 获取用户ID集。
     *
     * @param uid uid。
     * @return 用户ID集。
     */
    Set<String> users(String uid);

    /**
     * 创建新认证。
     *
     * @param userId 用户ID。
     * @param uid    UID值。
     * @param type   认证类型。
     * @param mobile 第三方账号手机号。
     * @param email  第三方头像Email。
     * @param nick   第三方账号昵称。
     * @param avatar 第三方头像URL。
     * @return 认证信息。
     */
    AuthModel create(String userId, String uid, String type, String mobile, String email, String nick, String avatar);

    /**
     * 更新认证。
     *
     * @param auth   认证信息。
     * @param type   认证类型。
     * @param mobile 第三方账号手机号。
     * @param email  第三方头像Email。
     * @param nick   第三方账号昵称。
     * @param avatar 第三方头像URL。
     */
    void update(AuthModel auth, String type, String mobile, String email, String nick, String avatar);

    /**
     * 根据uid检索认证信息。
     *
     * @param uid UID值。
     * @return 认证信息，如果不存在则返回null。
     */
    AuthModel findByUid(String uid);

    AuthModel findByAvatar(String avatar);

    /**
     * 查找用户。
     *
     * @param uid         UID。
     * @param defaultUser 默认用户。
     * @return 用户或默认用户。
     */
    String findUser(String uid, String defaultUser);

    /**
     * 删除当前认证。
     */
    void delete();

    /**
     * 删除用户认证。
     *
     * @param user 用户ID。
     */
    void delete(String user);
}
