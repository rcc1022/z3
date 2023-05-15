package org.lpw.clivia.alipay;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.AlipayConstants;
import com.alipay.api.AlipayRequest;
import com.alipay.api.AlipayResponse;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import org.lpw.clivia.payment.PaymentService;
import org.lpw.clivia.transfer.TransferListener;
import org.lpw.clivia.transfer.TransferModel;
import org.lpw.clivia.transfer.TransferService;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.CtrlHelper;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service(AlipayModel.NAME + ".service")
public class AlipayServiceImpl implements AlipayService, TransferListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Codec codec;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private CtrlHelper ctrlHelper;
    @Inject
    private UserService userService;
    @Inject
    private PaymentService paymentService;
    @Inject
    private TransferService transferService;
    @Inject
    private AlipayDao alipayDao;

    @Override
    public JSONArray query() {
        return modelHelper.toJson(alipayDao.query().getList());
    }

    @Override
    public AlipayModel findByKey(String key) {
        return alipayDao.findByKey(key);
    }

    @Override
    public AlipayModel findByAppId(String appId) {
        return alipayDao.findByAppId(appId);
    }

    @Override
    public JSONObject save(AlipayModel alipay) {
        AlipayModel model = alipayDao.findByKey(alipay.getKey());
        if (model == null) {
            model = new AlipayModel();
            model.setKey(alipay.getKey());
        }
        model.setName(alipay.getName());
        model.setAppId(alipay.getAppId());
        model.setPrivateKey(alipay.getPrivateKey());
        model.setPublicKey(alipay.getPublicKey());
        alipayDao.save(model);

        return modelHelper.toJson(model);
    }

    @Override
    public void delete(String id) {
        alipayDao.delete(id);
    }

    @Override
    public String quickWapPay(String key, String user, String subject, int amount, String billNo, String notice, String returnUrl) {
        AlipayModel alipay = alipayDao.findByKey(key);
        String content = getBizContent(alipay.getAppId(), user, subject, amount, billNo, notice, "QUICK_WAP_PAY");
        if (content == null)
            return null;

        AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
        request.setBizContent(content);

        return prepay(alipay, returnUrl, request);
    }

    @Override
    public String fastInstantTradePay(String key, String user, String subject, int amount, String billNo, String notice, String returnUrl) {
        AlipayModel alipay = alipayDao.findByKey(key);
        String content = getBizContent(alipay.getAppId(), user, subject, amount, billNo, notice, "FAST_INSTANT_TRADE_PAY");
        if (content == null)
            return null;

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setBizContent(content);

        return prepay(alipay, returnUrl, request);
    }

    @Override
    public String quickMsecurityPay(String key, String user, String subject, int amount, String billNo, String notice) {
        AlipayModel alipay = alipayDao.findByKey(key);
        String content = getBizContent(alipay.getAppId(), user, subject, amount, billNo, notice, "QUICK_MSECURITY_PAY");
        if (content == null)
            return null;

        AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
        request.setBizContent(content);
        String string = prepay(alipay, null, request);

        return string == null ? null : string.substring(string.indexOf('?') + 1, string.indexOf('>') - 1) + "&biz_content=" +
                codec.encodeUrl(string.substring(string.indexOf('{'), string.indexOf('}') + 1)
                        .replaceAll("&quot;", "\""), null);
    }

    private String getBizContent(String appId, String user, String subject, int amount, String billNo, String notice, String code) {
        if (validator.isEmpty(user))
            user = userService.id();
        JSONObject object = paymentService.create(getTransferType(), appId, user, amount, billNo, notice, null);
        String orderNo;
        if (!json.containsKey(object, "orderNo") || validator.isEmpty(orderNo = object.getString("orderNo")))
            return null;

        JSONObject obj = new JSONObject();
        obj.put("out_trade_no", orderNo);
        obj.put("subject", subject);
        obj.put("total_amount", numeric.toString(object.getIntValue("amount") * 0.01D, "0.00"));
        obj.put("product_code", code);

        return obj.toJSONString();
    }

    private String prepay(AlipayModel alipay, String returnUrl, AlipayRequest<? extends AlipayResponse> request) {
        String url = ctrlHelper.url("/alipay/notice");
        if (url == null) {
            logger.warn(null, "root未配置！");

            return null;
        }

        request.setNotifyUrl(url);
        if (!validator.isEmpty(returnUrl))
            request.setReturnUrl(returnUrl);

        try {
            return newAlipayClient(alipay).pageExecute(request).getBody();
        } catch (Throwable e) {
            logger.warn(e, "执行支付宝付款时发生异常！");

            return null;
        }
    }

    @Override
    public boolean notice(String appId, String orderNo, String tradeNo, String amount, String status, Map<String, String> map) {
        if (status.equals("WAIT_BUYER_PAY"))
            return false;

        AlipayModel alipay = alipayDao.findByAppId(appId);
        if (alipay == null)
            return false;

        try {
            if (!AlipaySignature.rsaCheckV1(map, alipay.getPublicKey(), "UTF-8", map.get(AlipayConstants.SIGN_TYPE)))
                return failure(null, alipay, map);
        } catch (AlipayApiException e) {
            return failure(e, alipay, map);
        }

        int state = status.equals("TRADE_SUCCESS") || status.equals("TRADE_FINISHED") ? 1 : 0;

        return json.has(paymentService.complete(orderNo, getAmount(amount), tradeNo, state, map), "orderNo", orderNo);
    }

    private boolean failure(Throwable e, AlipayModel alipay, Map<String, String> map) {
        logger.warn(e, "验证支付宝异步通知[{}:{}]签名失败！", modelHelper.toString(alipay), converter.toString(map));

        return false;

    }

    private int getAmount(String amount) {
        int indexOf = amount.indexOf('.');
        if (indexOf == -1)
            return numeric.toInt(amount) * 100;

        return numeric.toInt(amount.substring(0, indexOf)) * 100 + numeric.toInt(amount.substring(indexOf + 1)) % 100;
    }

    @Override
    public boolean transfer(String key, String user, String account, int amount, String billNo, String realName, String showName,
                            String remark, String notice, Map<String, String> map) {
        AlipayModel alipay = alipayDao.findByKey(key);
        JSONObject object = transferService.create(getTransferType(), alipay.getAppId(), validator.isEmpty(user) ? userService.id() : user,
                account, amount, billNo, notice, map);
        String orderNo;
        if (!json.containsKey(object, "orderNo") || validator.isEmpty(orderNo = object.getString("orderNo")))
            return false;

        JSONObject content = new JSONObject();
        content.put("out_biz_no", orderNo);
        content.put("payee_type", "ALIPAY_LOGONID");
        content.put("payee_account", account);
        content.put("amount", numeric.toString(amount * 0.01D, "0.00"));
        if (!validator.isEmpty(showName))
            content.put("payer_show_name", showName);
        if (!validator.isEmpty(realName))
            content.put("payee_real_name", realName);
        if (!validator.isEmpty(remark))
            content.put("remark", remark);
        AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
        request.setBizContent(content.toJSONString());
        try {
            return newAlipayClient(alipay).execute(request).isSuccess();
        } catch (AlipayApiException e) {
            logger.warn(e, "发送支付宝转账请求[{}]时发生异常！", content.toJSONString());

            return false;
        }
    }

    @Override
    public String getTransferType() {
        return "alipay";
    }

    @Override
    public void resetTransferState(TransferModel transfer) {
        AlipayModel alipay = findByAppId(transfer.getAppId());
        if (alipay == null)
            return;

        JSONObject content = new JSONObject();
        content.put("out_biz_no", transfer.getOrderNo());
        AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
        request.setBizContent(content.toJSONString());
        try {
            AlipayFundTransOrderQueryResponse response = newAlipayClient(alipay).execute(request);
            if (!response.isSuccess())
                return;

            String tradeNo = response.getOrderId();
            if (validator.isEmpty(tradeNo))
                return;

            int state = getState(response.getStatus());
            if (state == 0)
                return;

            transferService.complete(transfer.getOrderNo(), transfer.getAmount(), tradeNo, state, null);
        } catch (AlipayApiException e) {
            logger.warn(e, "查询支付宝转账[{}]结果时发生异常！", modelHelper.toJson(transfer));
        }
    }

    private int getState(String status) {
        return switch (status) {
            case "SUCCESS" -> 1;
            case "FAIL" -> 2;
            default -> 0;
        };
    }

    private AlipayClient newAlipayClient(AlipayModel alipay) {
        return new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", alipay.getAppId(), alipay.getPrivateKey(),
                "json", "UTF-8", alipay.getPublicKey(), AlipayConstants.SIGN_TYPE_RSA2);
    }
}
