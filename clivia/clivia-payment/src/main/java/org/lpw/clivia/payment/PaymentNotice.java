package org.lpw.clivia.payment;

import com.alibaba.fastjson.JSONObject;

/**
 * 支付完成通知。
 *
 */
public interface PaymentNotice {
    /**
     * 支付完成。
     *
     * @param payment 支付信息。
     * @param notice  通知配置。
     */
    void paymentDone(PaymentModel payment, JSONObject notice);
}
