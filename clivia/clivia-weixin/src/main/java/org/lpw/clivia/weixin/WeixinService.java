package org.lpw.clivia.weixin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.OutputStream;
import java.util.Map;
import java.util.function.Function;

public interface WeixinService {
    /**
     * 单个配置key。
     */
    String SINGLE_KEY = "weixin";
    /**
     * 微信key是否存在验证器Bean名称。
     * 默认错误信息key=WeixinModel.NAME+.not-exists。
     */
    String VALIDATOR_EXISTS = WeixinModel.NAME + ".validator.exists";
    /**
     * 微信APP ID是否不存在验证器Bean名称。
     * 默认错误信息key=WeixinModel.NAME+.exists。
     */
    String VALIDATOR_NOT_EXISTS = WeixinModel.NAME + ".validator.not-exists";

    /**
     * 获取微信配置集。
     *
     * @return 信配置集。
     */
    JSONArray query();

    /**
     * 根据引用key获取微信配置。
     *
     * @param key 引用key。
     * @return 微信配置；如果不存在则返回null。
     */
    WeixinModel findByKey(String key);

    /**
     * 根据APP ID获取微信配置。
     *
     * @param appId APP ID。
     * @return 微信配置；如果不存在则返回null。
     */
    WeixinModel findByAppId(String appId);

    /**
     * 保存微信配置。
     * 如果key存在则更新；key不存在则新增。
     *
     * @param weixin 微信配置。
     * @return 配置信息。
     */
    JSONObject save(WeixinModel weixin);

    /**
     * 获取单个配置。
     *
     * @return 单个配置，不存在则返回空JSON。
     */
    JSONObject single();

    /**
     * 保存单个配置。
     *
     * @param weixin 微信配置。
     */
    void singleSave(WeixinModel weixin);

    /**
     * 刷新AccessToken。
     *
     * @param key 引用key。
     */
    void refreshAccessToken(String key);

    /**
     * 创建公众号菜单。
     *
     * @param key 引用key。
     * @return 创建结果。
     */
    JSONObject menu(String key);

    /**
     * 删除微信配置。
     *
     * @param id ID值。
     */
    void delete(String id);

    /**
     * 验证echo信息是否正确。
     *
     * @param appId     微信公众号AppID。
     * @param signature 签名。
     * @param timestamp 时间戳。
     * @param nonce     随机数。
     * @param echostr   echo字符串。
     * @return 如果验证成功则返回echostr原值；否则返回"failure"。
     */
    String echo(String appId, String signature, String timestamp, String nonce, String echostr);

    /**
     * 处理微信通知。
     *
     * @param appId  微信公众号AppID。
     * @param string 通知消息。
     */
    void notice(String appId, String string);

    /**
     * 生成关注公众号二维码URL。
     *
     * @param key 配置key。
     * @return 关注公众号二维码URL。
     */
    String subscribeQr(String key);

    /**
     * 获取关注并登入信息。
     *
     * @return 微信用户信息；不存在则返回空JSON。
     */
    JSONObject subscribeSignIn();

    /**
     * 获取认证Code URL。
     *
     * @param key   配置key。
     * @param uri   回调URI。
     * @param scope 应用授权作用域。
     * @return 认证Code URL。
     */
    String code(String key, String uri, String scope);

    /**
     * 认证用户信息。
     *
     * @param key  配置key。
     * @param code 微信认证code。
     * @return 如果认证通过则返回用户授权信息，否则返回空JSON。
     */
    JSONObject auth(String key, String code);

    /**
     * 认证小程序用户信息。
     *
     * @param key      配置key。
     * @param code     微信认证code。
     * @param iv       加密算法的初始向量。
     * @param message  加密数据。
     * @param iv2      加密算法的初始向量。
     * @param message2 加密数据。
     * @return 如果认证通过则返回用户授权信息，否则返回空JSON。
     */
    JSONObject auth(String key, String code, String iv, String message, String iv2, String message2);

    /**
     * 获取绑定手机号。
     *
     * @param key  配置key。
     * @param code 微信认证code。
     * @return 如果认证通过则返回手机号信息，否则返回null。
     */
    JSONObject getPhoneNumber(String key, String code);

    /**
     * 生成支付二维码。
     *
     * @param key          引用key。
     * @param user         用户ID。
     * @param subject      订单名称。
     * @param amount       支付金额，单位：分。
     * @param billNo       单据号。
     * @param notice       异步通知。
     * @param size         二维码图片大小，小于等于0则使用默认值。
     * @param logo         LOGO图片名。
     * @param outputStream 输出流。
     */
    void prepayQrCode(String key, String user, String subject, int amount, String billNo, String notice, int size,
                      String logo, OutputStream outputStream);

    /**
     * 生成支付二维码；并输出Base64数据。
     *
     * @param key     引用key。
     * @param user    用户ID。
     * @param subject 订单名称。
     * @param amount  支付金额，单位：分。
     * @param billNo  单据号。
     * @param notice  异步通知。
     * @param size    二维码图片大小，小于等于0则使用默认值。
     * @param logo    LOGO图片名。
     * @return Base64数据，如果生成失败则返回null。
     */
    String prepayQrCodeBase64(String key, String user, String subject, int amount, String billNo, String notice, int size, String logo);

    /**
     * 生成APP支付参数。
     *
     * @param key     引用key。
     * @param user    用户ID。
     * @param subject 订单名称。
     * @param amount  支付金额，单位：分。
     * @param billNo  单据号。
     * @param notice  异步通知。
     * @return 支付参数。
     */
    JSONObject prepayApp(String key, String user, String subject, int amount, String billNo, String notice);

    /**
     * 生成JSAPI/小程序支付参数。
     *
     * @param key     引用key。
     * @param user    用户ID。
     * @param openId  微信OpenID。
     * @param subject 订单名称。
     * @param amount  支付金额，单位：分。
     * @param billNo  单据号。
     * @param notice  异步通知。
     * @return 支付参数。
     */
    JSONObject prepayJsapi(String key, String user, String openId, String subject, int amount, String billNo, String notice);

    /**
     * 生成H5支付参数。
     *
     * @param key     引用key。
     * @param user    用户ID。
     * @param subject 订单名称。
     * @param amount  支付金额，单位：分。
     * @param billNo  单据号。
     * @param notice  异步通知。
     * @return 支付URL。
     */
    String prepayH5(String key, String user, String subject, int amount, String billNo, String notice);

    /**
     * 异步通知。
     *
     * @param appId      APP ID。
     * @param orderNo    订单号。
     * @param tradeNo    网关订单号。
     * @param amount     金额。
     * @param returnCode 状态码。
     * @param resultCode 业务结果。
     * @param map        参数集。
     * @return 执行成功则返回true；否则返回false。
     */
    boolean notice(String appId, String orderNo, String tradeNo, String amount, String returnCode, String resultCode,
                   Map<String, String> map);

    /**
     * 解密AES-128-CBC/PKCS#7数据。
     *
     * @param key     配置key。
     * @param code    微信认证code。
     * @param iv      初始化参数。
     * @param message 加密数据。
     * @return 解密后的数据，如果解密失败则返回空JSON{}。
     */
    JSONObject decryptAesCbcPkcs7(String key, String code, String iv, String message);

    /**
     * 获取无限制二维码。
     *
     * @param key       引用key。
     * @param scene     场景。
     * @param page      页面。
     * @param width     宽度。
     * @param autoColor 自动配置线条颜色。
     * @param lineColor 线条颜色。
     * @param hyaline   是否透明底色。
     * @return 二维码地址。
     */
    String wxaCodeUnlimit(String key, String scene, String page, int width, boolean autoColor, JSONObject lineColor, boolean hyaline);

    /**
     * 获取JS API Ticket签名。
     *
     * @param key   引用key。
     * @param param 参数集。
     * @return 签名信息。
     */
    JSONObject jsapiTicketSignature(String key, JSONObject param);

    /**
     * 发送模板消息。
     *
     * @param key          引用key。
     * @param receiver     接收者。
     * @param templateId   模板ID。
     * @param url          跳转URL。
     * @param miniAppId    小程序APP ID。
     * @param miniPagePath 小程序页面路径。
     * @param data         模板数据。
     * @param color        颜色。
     * @return 发送结果。
     */
    JSONObject sendTemplateMessage(String key, String receiver, String templateId, String url, String miniAppId, String miniPagePath,
                                   JSONObject data, String color);

    /**
     * 发送小程序模板消息。
     *
     * @param key        引用key。
     * @param receiver   接收者。
     * @param templateId 模板ID。
     * @param page       页面。
     * @param formId     场景ID。
     * @param data       模板数据。
     * @param keyword    放大关键词。
     * @return 发送结果。
     */
    JSONObject sendMiniTemplateMessage(String key, String receiver, String templateId, String page, String formId,
                                       JSONObject data, String keyword);

    /**
     * 发送文本消息。
     *
     * @param key     引用key。
     * @param openId  接受者Open ID。
     * @param message 消息。
     * @return 发送结果。
     */
    JSONObject sendTextMessage(String key, String openId, String message);

    /**
     * 使用AccessToken请求。如果返回AccessToken过期[42001]则刷新AccessToken。
     *
     * @param weixin   微信配置。
     * @param function 处理函数。
     * @return 请求结果。
     */
    JSONObject byAccessToken(WeixinModel weixin, Function<String, String> function);

    /**
     * 获取同步数据。
     *
     * @param uri       同步URI。
     * @param parameter 参数集。
     * @return 同步数据。
     */
    JSONObject sync(String uri, Map<String, String> parameter);
}
