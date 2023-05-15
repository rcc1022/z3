package org.lpw.clivia.weixin;

import com.alibaba.fastjson.JSONObject;
import com.wechat.pay.contrib.apache.httpclient.WechatPayHttpClientBuilder;
import com.wechat.pay.contrib.apache.httpclient.auth.AutoUpdateCertificatesVerifier;
import com.wechat.pay.contrib.apache.httpclient.auth.PrivateKeySigner;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Credentials;
import com.wechat.pay.contrib.apache.httpclient.auth.WechatPay2Validator;
import com.wechat.pay.contrib.apache.httpclient.util.PemUtil;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.ctrl.CtrlHelper;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.security.PrivateKey;
import java.util.List;

@Service(WeixinModel.NAME + ".pay")
public class WeixinPayImpl implements WeixinPay {
    private static class Prepay {
        private WeixinModel weixin;
        private JSONObject object;
        private Object failure;
    }

    private static final String[] NAMES_JSAPI = {"appId", "timeStamp", "nonceStr", "package"};
    private static final String[] NAMES_APP = {"appidd", "timestamp", "noncestr", "prepayid"};

    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Message message;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Codec codec;
    @Inject
    private Logger logger;
    @Inject
    private Digest digest;
    @Inject
    private CtrlHelper ctrlHelper;
    @Inject
    private Templates templates;
    @Inject
    private WeixinDao weixinDao;

    @Override
    public Object jsapi(String key, String description, String orderNo, int amount, String openId) {
        JSONObject object = new JSONObject();
        JSONObject payer = new JSONObject();
        payer.put("openid", openId);
        object.put("payer", payer);
        Prepay prepay = prepay(key, "jsapi", description, orderNo, amount, object);
        if (prepay.object == null)
            return prepay.failure;

        JSONObject pack = pack(prepay.weixin, NAMES_JSAPI, "prepay_id=" + prepay.object.getString("prepay_id"));
        pack.put("signType", "RSA");

        return pack;
    }

    @Override
    public Object app(String key, String description, String orderNo, int amount) {
        Prepay prepay = prepay(key, "app", description, orderNo, amount, null);
        if (prepay.object == null)
            return prepay.failure;

        JSONObject pack = pack(prepay.weixin, NAMES_APP, prepay.object.getString("prepay_id"));
        pack.put("package", "Sign=WXPay");
        pack.put("partnerid", prepay.weixin.getMchPartnerId());

        return pack;
    }

    @Override
    public Object h5(String key, String description, String orderNo, int amount) {
        Prepay prepay = prepay(key, "h5", description, orderNo, amount, null);

        return prepay.object == null ? prepay.failure : prepay.object.getString("h5_url");
    }

    @Override
    public Object qrcode(String key, String description, String orderNo, int amount) {
        Prepay prepay = prepay(key, "native", description, orderNo, amount, null);

        return prepay.object == null ? prepay.failure : prepay.object.getString("code_url");
    }

    @Override
    public Object mini(String key, String description, String orderNo, int amount, String openId) {
        return jsapi(key, description, orderNo, amount, openId);
    }

    private Prepay prepay(String key, String type, String description, String orderNo, int total, JSONObject object) {
        Prepay prepay = new Prepay();
        prepay.weixin = findOrRandom(key);
        if (prepay.weixin == null) {
            logger.warn(null, "无法获得微信配置[{}]。", key);
            prepay.failure = failure(901, ".key.not-exists");

            return prepay;
        }

        JSONObject obj = new JSONObject();
        obj.put("appid", prepay.weixin.getAppId());
        obj.put("mchid", prepay.weixin.getMchId());
        obj.put("description", description);
        obj.put("out_trade_no", orderNo);
        obj.put("notify_url", ctrlHelper.url("/weixin/notice-v3"));
        JSONObject amount = new JSONObject();
        amount.put("total", total);
        obj.put("amount", amount);
        if (!validator.isEmpty(object))
            obj.putAll(object);

        HttpPost post = new HttpPost("https://api.mch.weixin.qq.com/v3/pay/transactions/" + type);
        post.setEntity(new StringEntity(obj.toJSONString(), (String) null));
        String string = execute(prepay.weixin, post);
        if (string == null) {
            prepay.failure = failure(999, ".execute.failure");

            return prepay;
        }

        prepay.object = json.toObject(string);
        if (prepay.object == null)
            prepay.failure = string;

        return prepay;
    }

    private WeixinModel findOrRandom(String key) {
        if (validator.isEmpty(key)) {
            List<WeixinModel> list = weixinDao.query().getList();
            if (list.isEmpty())
                return null;

            return list.get(generator.random(0, list.size() - 1));
        }

        return weixinDao.findByKey(key);
    }

    private String execute(WeixinModel weixin, HttpUriRequest request) {
        PrivateKey privateKey = PemUtil.loadPrivateKey(new ByteArrayInputStream(weixin.getMchPrivateKey().getBytes()));
        try (CloseableHttpResponse response = WechatPayHttpClientBuilder.create().withMerchant(weixin.getMchId(), weixin.getMchSerialNo(), privateKey)
                .withValidator(new WechatPay2Validator(new AutoUpdateCertificatesVerifier(new WechatPay2Credentials(weixin.getMchId(),
                        new PrivateKeySigner(weixin.getMchSerialNo(), privateKey)), weixin.getMchKeyV3().getBytes()))).build().execute(request)) {
            HttpEntity httpEntity = response.getEntity();
            if (httpEntity == null) {
                request.abort();
                logger.warn(null, "执行HTTP请求[{}:{}:{}]未返回数据！", request.getMethod(), request.getURI(), response.getStatusLine().getStatusCode());

                return null;
            }

            return io.readAsString(httpEntity.getContent());
        } catch (Throwable throwable) {
            request.abort();
            logger.warn(throwable, "执行HTTP请求时发生异常！");

            return null;
        }
    }

    private Object failure(int code, String message) {
        return templates.get().failure(155000 + code, this.message.get(WeixinModel.NAME + message), null, null);
    }

    private JSONObject pack(WeixinModel weixin, String[] names, String prepay) {
        JSONObject object = new JSONObject();
        object.put(names[0], weixin.getAppId());
        long timestamp = System.currentTimeMillis() / 1000;
        object.put(names[1], timestamp);
        String nonce = generator.random(32);
        object.put(names[2], nonce);
        object.put(names[3], prepay);
        object.put("paySign", codec.encodeBase64(digest.sha256Rsa(weixin.getMchPrivateKey().getBytes(),
                (weixin.getAppId() + '\n' + timestamp + '\n' + nonce + '\n' + prepay + '\n').getBytes())));

        return object;
    }
}
