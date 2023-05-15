package org.lpw.clivia.user;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.dao.orm.PageList;

import java.sql.Date;
import java.util.List;
import java.util.Set;

public interface UserService {
    String SYSTEM_ID = "00000000-0000-0000-0000-000000000000";
    /**
     * 类型是否存在验证器Bean名称。
     */
    String VALIDATOR_EXISTS_TYPE = UserModel.NAME + ".validator.exists-type";
    /**
     * 是否允许注册验证器Bean名称。
     */
    String VALIDATOR_SIGN_UP_ENABLE = UserModel.NAME + ".validator.sign-up.enable";
    /**
     * 注册短信验证码验证器Bean名称。
     */
    String VALIDATOR_SIGN_UP_SMS = UserModel.NAME + ".validator.sign-up.sms";
    /**
     * 登入验证器Bean名称。
     */
    String VALIDATOR_SIGN_IN = UserModel.NAME + ".validator.sign-in";
    /**
     * 已登入验证器Bean名称。
     */
    String VALIDATOR_SIGN = UserModel.NAME + ".validator.sign";
    /**
     * 密码验证器Bean名称。
     */
    String VALIDATOR_PASSWORD = UserModel.NAME + ".validator.password";
    /**
     * 用户信息是否存在验证器Bean名称。
     */
    String VALIDATOR_EXISTS = UserModel.NAME + ".validator.exists";
    /**
     * 用户信息是否存在或已登入验证器Bean名称。
     */
    String VALIDATOR_EXISTS_SIGN = VALIDATOR_EXISTS + "-sign";
    /**
     * 登入短信验证码Scene。
     */
    String SMS_SIGN_IN = "sign-in";
    /**
     * 注册短信验证码Scene。
     */
    String SMS_SIGN_UP = "sign-up";
    /**
     * 重置密码验证码Scene。
     */
    String SMS_RESET_PASSWORD = "reset-password";

    /**
     * 验证是否为code。
     *
     * @param code code。
     * @return 如果是则返回true；否则返回false。
     */
    boolean isCode(String code);

    /**
     * 是否已达到最大用户数。
     *
     * @return 如果已达到则返回true；否则返回false。
     */
    boolean isFull();

    /**
     * 设置最大用户数。
     *
     * @param full 最大用户数，小于等于0表示不限制。
     */
    void setFull(int full);

    /**
     * 注册。
     *
     * @param uid        UID值。
     * @param password   密码。
     * @param type       认证类型。
     * @param inviter    推荐人。
     * @param grade      等级。
     * @param invitecode 邀请码。
     * @return 用户信息。
     */
    UserModel signUp(String uid, String password, String type, String inviter, String grade, String invitecode);

    /**
     * 登入验证。
     *
     * @param uid      UID值。
     * @param password 密码。
     * @param type     认证类型。
     * @param grade    等级。
     * @return 认证结果。
     */
    int signIn(String uid, String password, String type, String grade);

    /**
     * 密码登入验证。
     *
     * @param id       ID值。
     * @param password 密码。
     * @return 认证成功则返回true；否则返回false。
     */
    boolean signInPassword(String id, String password);

    /**
     * 手势登入验证。
     *
     * @param id      ID值。
     * @param gesture 手势密码。
     * @return 认证成功则返回true；否则返回false。
     */
    boolean signInGesture(String id, String gesture);

    /**
     * 获取当前用户登入信息。
     *
     * @return 当前用户登入信息；如果未登入则返回空JSON数据。
     */
    JSONObject sign();

    /**
     * 获取当前用户ID。
     *
     * @return 当前用户ID。
     */
    String id();

    /**
     * 获取当前用户等级。
     *
     * @return 当前用户等级。
     */
    int grade();

    /**
     * 登出。
     */
    void signOut();

    /**
     * 登出。
     *
     * @param sid Session ID。
     */
    void signOut(String sid);

    /**
     * 修改当前用户信息。
     *
     * @param user 用户信息。
     */
    void modify(UserModel user);

    /**
     * 修改密码。
     *
     * @param oldPassword 旧密码。
     * @param newPassword 新密码。
     * @return 如果修改成功则返回true；否则返回false。
     */
    boolean password(String oldPassword, String newPassword);

    /**
     * 更新手机号。
     *
     * @param mobile 手机号。
     */
    void mobile(String mobile);

    /**
     * 设置密码。
     *
     * @param mobile   手机号。
     * @param password 新密码。
     */
    void resetPassword(String mobile, String password);

    /**
     * 加密密码。
     *
     * @param password 密码明文。
     * @return 加密后的密码。
     */
    String password(String password);

    /**
     * 获取当前Session中用户对象。
     *
     * @return 用户对象；不存在则返回null。
     */
    UserModel fromSession();

    /**
     * 获取当前用户登入UID。
     *
     * @return 当前用户登入UID。
     */
    String uidFromSession();

    /**
     * 获取用户数据。
     *
     * @param id ID值。
     * @return 用户数据；不存在则返回null。
     */
    UserModel findById(String id);

    /**
     * 获取用户数据集。
     *
     * @param ids ID集。
     * @return 用户数据集。
     */
    JSONObject get(String[] ids);

    /**
     * 获取用户数据。
     *
     * @param id ID值。
     * @return 用户数据；不存在则返回空JSON。
     */
    JSONObject get(String id);

    JSONObject getNickAvatar(String id);

    /**
     * 根据编码获得用户信息。
     *
     * @param code 唯一编码。
     * @return 用户信息；如果不存在则返回null。
     */
    UserModel findByCode(String code);

    /**
     * 获取用户数据。
     *
     * @param uid UID值。
     * @return 用户数据；不存在则返回空JSON。
     */
    JSONObject findByUid(String uid);

    /**
     * 查找用户或当前用户。
     *
     * @param idUidCode 用户ID或UID或code值。
     * @return 用户信息，不存在且未登入则返回空JSON{}。
     */
    JSONObject findOrSign(String idUidCode);

    /**
     * 查找用户。
     *
     * @param idUidCode 用户ID或UID或code值。
     * @return 用户信息，不存在则返回空JSON{}。
     */
    JSONObject find(String idUidCode);

    /**
     * 填充数据。
     *
     * @param array 数组数组。
     * @param names 填充的属性集。
     * @return 填充后的数据数组。
     */
    JSONArray fill(JSONArray array, String[] names);

    /**
     * 检索用户信息集。
     *
     * @param uid      UID。
     * @param idcard   身份证号；为空则表示所有。
     * @param name     姓名；为空则表示所有。
     * @param nick     昵称；为空则表示所有。
     * @param mobile   用户手机号；为空则表示所有。
     * @param email    Email地址；为空则表示所有。
     * @param weixin   微信号；为空则表示所有。
     * @param qq       QQ号；为空则表示所有。
     * @param code     唯一编码；为空则表示所有。
     * @param minGrade 最小等级，-1表示不限制。
     * @param maxGrade 最大等级，-1表示不限制。
     * @param state    状态：-1-所有；0-正常；1-禁用。
     * @param register 注册日期范围，格式：yyyy-MM-dd；为空表示不限制。
     * @param from     来源。
     * @return 用户信息集。
     */
    JSONObject query(String uid, String idcard, String name, String nick, String mobile, String email,
                     String weixin, String qq, String code, int minGrade, int maxGrade, int state, String register,
                     String from);

    /**
     * 获取指定等级用户集。
     *
     * @param grade 目标等级。
     * @return 用户集。
     */
    List<UserModel> list(int grade);

    /**
     * 检索用户ID集。
     *
     * @param uid    UID。
     * @param idcard 身份证号；为空则表示所有。
     * @param name   姓名；为空则表示所有。
     * @param nick   昵称；为空则表示所有。
     * @param mobile 用户手机号；为空则表示所有。
     * @param email  Email地址；为空则表示所有。
     * @param weixin 微信号；为空则表示所有。
     * @param qq     QQ号；为空则表示所有。
     * @param code   唯一编码；为空则表示所有。
     * @return 用户ID集。
     */
    Set<String> ids(String uid, String idcard, String name, String nick, String mobile, String email,
                    String weixin, String qq, String code);

    /**
     * 获取推荐用户集。
     *
     * @param inviter  推荐人。
     * @param pageSize 每页记录数。
     * @param pageNum  当前页数。
     * @return 推荐用户集。
     */
    PageList<UserModel> inviter(String inviter, int pageSize, int pageNum);

    /**
     * 更新用户信息。
     *
     * @param user 用户信息。
     */
    void update(UserModel user);

    /**
     * 重置密码。
     *
     * @param id 用户ID。
     * @return 新密码。
     */
    String resetPassword(String id);

    /**
     * 更新用户信息。
     *
     * @param id     ID值。
     * @param idcard 身份证号。
     * @param name   姓名。
     * @param nick   昵称。
     * @param mobile 手机号。
     * @param email  Email地址。
     * @param weixin 微信号。
     * @param qq     QQ号。
     * @param gender 性别：0-未知；1-男；2-女。
     */
    void info(String id, String idcard, String name, String nick, String mobile, String email, String weixin,
              String qq, String signature, int gender);

    /**
     * 设置用户等级。
     *
     * @param id    ID值。
     * @param grade 等级值。
     */
    void grade(String id, int grade);

    /**
     * 设置当前用户等级。
     *
     * @param grade 等级值。
     */
    void grade(int grade);

    /**
     * 设置用户状态。
     *
     * @param id    ID值。
     * @param state 状态值。
     */
    void state(String id, int state);

    /**
     * 重置root用户。
     *
     * @param user     用户。
     * @param password 密码。
     * @return 重置成功则返回true；否则返回false。
     */
    boolean root(UserModel user, String password);

    /**
     * 统计用户数。
     *
     * @return 用户数。
     */
    int count();

    /**
     * 统计用户注册数。
     *
     * @param date 日期。
     * @return 注册数。
     */
    int count(Date date);

    /**
     * 创建用户。
     *
     * @param uid      UID。
     * @param password 密码。
     * @param idcard   身份证号。
     * @param name     姓名。
     * @param nick     昵称。
     * @param mobile   手机号。
     * @param email    Email地址。
     * @param weixin   微信号。
     * @param qq       QQ号。
     * @param avatar   头像。
     * @param gender   性别：0-未知；1-男；2-女。
     * @param birthday 出生日期。
     * @param inviter  邀请人。
     * @param grade    等级。
     * @param state    状态。
     * @return 用户信息。
     */
    UserModel create(String uid, String password, String idcard, String name, String nick, String mobile,
                     String email, String weixin, String qq, String avatar, int gender, Date birthday,
                     String inviter, int grade, int state);

    /**
     * 创建用户。
     *
     * @param user 用户。
     */
    void insert(UserModel user);

    /**
     * 删除。
     *
     * @param id ID值。
     */
    void delete(String id);

    /**
     * 注销。
     *
     * @param password 登入密码。
     * @return 注销成功则返回true；否则返回false。
     */
    boolean destroy(String password);

    /**
     * 清空当前用户缓存数据。
     */
    void clearCache();

    /**
     * 清空缓存。
     *
     * @param id ID值。
     */
    void clearCache(String id);
}
