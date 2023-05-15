package org.lpw.clivia.transfer;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface TransferService {
    /**
     * 订单号是否存在验证器Bean名称。
     * 默认错误信息key=TransferModel.NAME+.not-exists。
     */
    String VALIDATOR_EXISTS = TransferModel.NAME + ".validator.exists";
    /**
     * 是否可以设置为成功验证器Bean名称。
     * 默认错误信息key=TransferModel.NAME+success.disable。
     */
    String VALIDATOR_SUCCESS = TransferModel.NAME + ".validator.success";

    /**
     * 检索订单信息集。
     *
     * @param type    类型，为空则表示全部。
     * @param appId   支付APP ID。
     * @param user    用户ID或UID，为空则表示全部。
     * @param orderNo 订单号，为空则表示全部。
     * @param billNo  单据号，为空则表示全部。
     * @param tradeNo 网关订单号，为空则表示全部。
     * @param state   状态，-1则表示全部。
     * @param start   开始时间范围。
     * @return 订单信息集。
     */
    JSONObject query(String type, String appId, String user, String orderNo, String billNo, String tradeNo, int state, String start);

    /**
     * 设置订单信息为成功。
     *
     * @param id  订单ID。
     * @param map 参数集。
     * @return 订单信息。
     */
    JSONObject success(String id, Map<String, String> map);

    /**
     * 发送通知。
     *
     * @param id 订单ID。
     * @return 订单信息。
     */
    JSONObject notice(String id);

    /**
     * 查找支付订单信息。
     *
     * @param uk 订单唯一索引值，可以是ID或订单号。
     * @return 支付订单信息；如果未找到则返回null。
     */
    TransferModel find(String uk);

    /**
     * 创建支付订单。
     *
     * @param type    类型。
     * @param user    用户ID；如果为空则使用当前用户ID。
     * @param account 账户。
     * @param appId   支付APP ID。
     * @param amount  金额，单位：分。
     * @param billNo  单据号。
     * @param notice  通知。
     * @param map     参数集。
     * @return 订单信息。
     */
    JSONObject create(String type, String appId, String user, String account, int amount, String billNo, String notice, Map<String, String> map);

    /**
     * 订单完成。
     *
     * @param orderNo 订单号。
     * @param amount  金额，单位：分。
     * @param tradeNo 网关订单号。
     * @param state   状态：1-成功；2-失败。
     * @param map     参数集。
     * @return 订单信息；如果处理失败则返回空JSON。
     */
    JSONObject complete(String orderNo, int amount, String tradeNo, int state, Map<String, String> map);
}
