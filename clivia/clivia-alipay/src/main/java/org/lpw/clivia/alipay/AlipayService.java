package org.lpw.clivia.alipay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface AlipayService {
    /**
     * 支付宝APP ID是否不存在验证器Bean名称。
     * 默认错误信息key=AlipayModel.NAME+.not-exists。
     */
    String VALIDATOR_EXISTS = AlipayModel.NAME + ".validator.exists";

    /**
     * 支付宝APP ID是否不存在验证器Bean名称。
     * 默认错误信息key=AlipayModel.NAME+.exists。
     */
    String VALIDATOR_NOT_EXISTS = AlipayModel.NAME + ".validator.not-exists";

    /**
     * 获取支付宝配置集。
     *
     * @return 信配置集。
     */
    JSONArray query();

    /**
     * 根据KEY获取支付宝配置。
     *
     * @param key 引用KEY。
     * @return 支付宝配置；如果不存在则返回null。
     */
    AlipayModel findByKey(String key);

    /**
     * 根据APP ID获取支付宝配置。
     *
     * @param appId APP ID。
     * @return 支付宝配置；如果不存在则返回null。
     */
    AlipayModel findByAppId(String appId);

    /**
     * 保存支付宝配置。
     * 如果key存在则更新；key不存在则新增。
     *
     * @param alipay 支付宝配置。
     * @return 配置值。
     */
    JSONObject save(AlipayModel alipay);

    /**
     * 删除支付宝配置。
     *
     * @param id ID值。
     */
    void delete(String id);

    /**
     * 发起手机WEB端支付。
     *
     * @param key       引用key。
     * @param user      用户ID。
     * @param subject   订单名称。
     * @param amount    支付金额，单位：分。
     * @param billNo    单据号。
     * @param notice    异步通知。
     * @param returnUrl 同步结果URL地址。
     * @return 支付内容；如果发起失败则返回null。
     */
    String quickWapPay(String key, String user, String subject, int amount, String billNo, String notice, String returnUrl);

    /**
     * 发起PC WEB端支付。
     *
     * @param key       引用key。
     * @param user      用户ID。
     * @param subject   订单名称。
     * @param amount    支付金额，单位：分。
     * @param billNo    单据号。
     * @param notice    异步通知。
     * @param returnUrl 同步结果URL地址。
     * @return 支付内容；如果发起失败则返回null。
     */
    String fastInstantTradePay(String key, String user, String subject, int amount, String billNo, String notice, String returnUrl);

    /**
     * 发起APP端支付。
     *
     * @param key     引用key。
     * @param user    用户ID。
     * @param subject 订单名称。
     * @param amount  支付金额，单位：分。
     * @param notice  异步通知。
     * @return 支付内容；如果发起失败则返回null。
     */
    String quickMsecurityPay(String key, String user, String subject, int amount, String billNo, String notice);

    /**
     * 异步通知。
     *
     * @param appId   APP ID。
     * @param orderNo 订单号。
     * @param tradeNo 网关订单号。
     * @param amount  金额。
     * @param status  状态。
     * @param map     参数集。
     * @return 执行成功则返回true；否则返回false。
     */
    boolean notice(String appId, String orderNo, String tradeNo, String amount, String status, Map<String, String> map);

    /**
     * 转账到账户。
     *
     * @param key      引用key。
     * @param user     用户。
     * @param account  支付宝登录号，支持邮箱和手机号格式。
     * @param amount   金额，单位：分。
     * @param billNo   单据号。
     * @param realName 真实姓名，不为空时校验姓名与账号是否匹配，空时不校验。
     * @param showName 显示名称，为空显示付款方的支付宝认证姓名或单位名称。
     * @param remark   备注，转账金额大于等于50000必须。
     * @param notice   异步通知。
     * @param map      参数集。
     * @return 提交结果，不代表转账结果。
     */
    boolean transfer(String key, String user, String account, int amount, String billNo, String realName, String showName,
                     String remark, String notice, Map<String, String> map);
}
