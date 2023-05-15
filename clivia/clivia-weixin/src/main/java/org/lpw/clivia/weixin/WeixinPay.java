package org.lpw.clivia.weixin;

/**
 * 微信支付。
 *
 */
public interface WeixinPay {
    /**
     * 公众号支付。
     *
     * @param key         引用KEY。
     * @param description 商品描述。
     * @param orderNo     商户订单号。
     * @param amount      订单金额，单位：分。
     * @param openId      支付者Open ID。
     * @return 调起支付所需的参数。
     */
    Object jsapi(String key, String description, String orderNo, int amount, String openId);

    /**
     * APP支付。
     *
     * @param key         引用KEY。
     * @param description 商品描述。
     * @param orderNo     商户订单号。
     * @param amount      订单金额，单位：分。
     * @return 调起支付所需的参数。
     */
    Object app(String key, String description, String orderNo, int amount);

    /**
     * H5支付。
     *
     * @param key         引用KEY。
     * @param description 商品描述。
     * @param orderNo     商户订单号。
     * @param amount      订单金额，单位：分。
     * @return H5 URL地址。
     */
    Object h5(String key, String description, String orderNo, int amount);

    /**
     * 二维码支付。
     *
     * @param key         引用KEY。
     * @param description 商品描述。
     * @param orderNo     商户订单号。
     * @param amount      订单金额，单位：分。
     * @return 二维码内容。
     */
    Object qrcode(String key, String description, String orderNo, int amount);

    /**
     * 小程序支付。
     *
     * @param key         引用KEY。
     * @param description 商品描述。
     * @param orderNo     商户订单号。
     * @param amount      订单金额，单位：分。
     * @param openId      支付者Open ID。
     * @return 调起支付所需的参数。
     */
    Object mini(String key, String description, String orderNo, int amount, String openId);
}
