package org.lpw.clivia.weixin;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.lpw.clivia.lock.LockHelper;
import org.lpw.clivia.payment.PaymentService;
import org.lpw.clivia.temporary.Temporary;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.weixin.info.InfoModel;
import org.lpw.clivia.weixin.info.InfoService;
import org.lpw.clivia.weixin.reply.ReplyService;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.crypto.Sign;
import org.lpw.photon.ctrl.CtrlHelper;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.ctrl.http.ServiceHelper;
import org.lpw.photon.ctrl.upload.UploadService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.storage.Storages;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.QrCode;
import org.lpw.photon.util.Thread;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.lpw.photon.util.Xml;
import org.lpw.photon.wormhole.WormholeHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service(WeixinModel.NAME + ".service")
public class WeixinServiceImpl implements WeixinService, ContextRefreshedListener, HourJob, MinuteJob {
    private static final String CACHE_TICKET_SESSION_ID = WeixinModel.NAME + ".ticket-session-id:";
    private static final String SESSION_SUBSCRIBE_SIGN_IN = WeixinModel.NAME + ".subscribe-sign-in";
    private static final String SESSION_MINI = WeixinModel.NAME + ".mini";
    private static final String SESSION_MINI_SESSION_KEY = WeixinModel.NAME + ".mini.session-key";

    @Inject
    private Digest digest;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Generator generator;
    @Inject
    private Validator validator;
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private DateTime dateTime;
    @Inject
    private Io io;
    @Inject
    private Xml xml;
    @Inject
    private Sign sign;
    @Inject
    private Thread thread;
    @Inject
    private QrCode qrCode;
    @Inject
    private Codec codec;
    @Inject
    private Context context;
    @Inject
    private Cache cache;
    @Inject
    private Logger logger;
    @Inject
    private Storages storages;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private WormholeHelper wormholeHelper;
    @Inject
    private CtrlHelper ctrlHelper;
    @Inject
    private Header header;
    @Inject
    private Session session;
    @Inject
    private UploadService uploadService;
    @Inject
    private Temporary temporary;
    @Inject
    private LockHelper lockHelper;
    @Inject
    private UserService userService;
    @Inject
    private PaymentService paymentService;
    @Inject
    private InfoService infoService;
    @Inject
    private ReplyService replyService;
    @Inject
    private WeixinDao weixinDao;
    @Value("${" + WeixinModel.NAME + ".auto:false}")
    private boolean auto;
    @Value("${" + WeixinModel.NAME + ".synch.url:}")
    private String synchUrl;
    @Value("${" + WeixinModel.NAME + ".synch.key:}")
    private String synchKey;
    @Value("${" + WeixinModel.NAME + ".require-union-id:false}")
    private boolean requireUnionId;
    @Value("${" + WeixinModel.NAME + ".qr-code.size:256}")
    private int qrCodeSize;
    @Value("${" + WeixinModel.NAME + ".qr-code.logo:}")
    private String qrCodeLogo;

    @Override
    public JSONArray query() {
        return modelHelper.toJson(weixinDao.query().getList());
    }

    @Override
    public WeixinModel findByKey(String key) {
        return weixinDao.findByKey(key);
    }

    @Override
    public WeixinModel findByAppId(String appId) {
        return weixinDao.findByAppId(appId);
    }

    @Override
    public JSONObject save(WeixinModel weixin) {
        WeixinModel model = weixinDao.findByKey(weixin.getKey());
        if (model == null) {
            model = new WeixinModel();
            model.setKey(weixin.getKey());
        }
        model.setName(weixin.getName());
        model.setAppId(weixin.getAppId());
        model.setSecret(weixin.getSecret());
        model.setToken(weixin.getToken());
        model.setMchId(weixin.getMchId());
        model.setMchPartnerId(weixin.getMchPartnerId());
        model.setMchKey(weixin.getMchKey());
        model.setMchSerialNo(weixin.getMchSerialNo());
        model.setMchPrivateKey(weixin.getMchPrivateKey());
        model.setMchKeyV3(weixin.getMchKeyV3());
        model.setMenu(weixin.getMenu());
        weixinDao.save(model);
        if (auto && validator.isEmpty(synchUrl))
            refreshAccessToken(model);

        return modelHelper.toJson(model);
    }

    @Override
    public JSONObject single() {
        WeixinModel weixin = weixinDao.findByKey(SINGLE_KEY);

        return weixin == null ? new JSONObject() : modelHelper.toJson(weixin);
    }

    @Override
    public void singleSave(WeixinModel weixin) {
        weixin.setKey(SINGLE_KEY);
        weixin.setName(SINGLE_KEY);
        save(weixin);
    }

    @Override
    public void refreshAccessToken(String key) {
        refreshAccessToken(findByKey(key));
    }

    @Override
    public JSONObject menu(String key) {
        WeixinModel weixin = findByKey(key);
        if (weixin == null || validator.isEmpty(weixin.getMenu()))
            return new JSONObject();

        return byAccessToken(weixin, accessToken -> {
            String string = http.post("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken,
                    null, weixin.getMenu());
            if (logger.isInfoEnable())
                logger.info("创建微信公众号菜单[{}:{}:{}]。", weixin.getKey(), weixin.getMenu(), string);

            return string;
        });
    }

    @Override
    public void delete(String id) {
        weixinDao.delete(id);
    }

    @Override
    public String echo(String appId, String signature, String timestamp, String nonce, String echostr) {
        WeixinModel weixin = weixinDao.findByAppId(appId);
        if (weixin == null)
            return "failure";

        List<String> list = new ArrayList<>();
        list.add(weixin.getToken());
        list.add(timestamp);
        list.add(nonce);
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        list.forEach(sb::append);
        boolean success = digest.sha1(sb.toString()).equals(signature);

        if (logger.isInfoEnable())
            logger.info("验证微信服务[{}:signature={};timestamp={};nonce={};echostr={}]。",
                    success, signature, timestamp, nonce, echostr);

        return success ? echostr : "failure";
    }

    @Override
    public void notice(String appId, String string) {
        if (validator.isEmpty(string))
            return;

        WeixinModel weixin = weixinDao.findByAppId(appId);
        if (weixin == null) {
            logger.warn(null, "无法获得微信[{}:{}]配置！", appId, string);

            return;
        }

        string = string.replaceAll(">\\s+<", "><");
        if (logger.isDebugEnable())
            logger.debug("收到微信[{}:{}]通知[{}]。", weixin.getKey(), weixin.getAppId(), string);

        String msgType = getValue(string, "<MsgType><![CDATA[", "]]></MsgType>");
        if (msgType == null) {
            logger.warn(null, "无法获得微信消息类型[{}]信息！", string);

            return;
        }

        switch (msgType) {
            case "event" -> event(weixin, string);
            case "text" -> replyService.send(weixin, getOpenId(string), "text",
                    getValue(string, "<Content><![CDATA[", "]]></Content>"), null);
            case "miniprogrampage" -> replyService.send(weixin, getOpenId(string), "miniprogrampage",
                    getValue(string, "<PagePath><![CDATA[", "]]></PagePath>"), null);
        }
    }

    private void event(WeixinModel weixin, String string) {
        String event = getValue(string, "<Event><![CDATA[", "]]></Event>");
        if (event == null) {
            logger.warn(null, "无法获得微信事件类型[{}]信息！", string);

            return;
        }

        String eventKey = getValue(string, "<EventKey><![CDATA[", "]]></EventKey>");
        if (eventKey != null && eventKey.startsWith("qrscene_"))
            eventKey = eventKey.substring(8);
        if (event.equals("subscribe") || event.equals("SCAN"))
            signIn(weixin, string, event, eventKey);
    }

    private void signIn(WeixinModel weixin, String string, String event, String eventKey) {
        String openId = getOpenId(string);
        if (openId == null) {
            logger.warn(null, "无法获得微信消息[{}]中Open ID信息！", string);

            return;
        }

        replyService.send(weixin, openId, "event", event, eventKey);
        JSONObject object = new JSONObject();
        if (getInfoFail(weixin, object, openId))
            return;

        saveInfo(weixin, object, openId);
        String ticket = getValue(string, "<Ticket><![CDATA[", "]]></Ticket>");
        if (ticket == null) {
            logger.warn(null, "无法获得微信消息[{}]中Ticket信息！", string);

            return;
        }

        String sessionId = cache.get(CACHE_TICKET_SESSION_ID + ticket);
        if (validator.isEmpty(sessionId)) {
            logger.warn(null, "无法获得微信消息Ticket[{}]缓存的Session ID！", ticket);

            return;
        }

        session.set(sessionId, SESSION_SUBSCRIBE_SIGN_IN, object);
    }

    private String getOpenId(String string) {
        return getValue(string, "<FromUserName><![CDATA[", "]]></FromUserName>");
    }

    private String getValue(String string, String prefix, String suffix) {
        int prefixIndexOf = string.indexOf(prefix);
        int suffixIndexOf = string.indexOf(suffix);

        return prefixIndexOf == -1 || suffixIndexOf == -1 ? null : string.substring(prefixIndexOf + prefix.length(), suffixIndexOf);
    }

    private boolean getInfoFail(WeixinModel weixin, JSONObject object, String openId) {
        for (int i = 0; i < 5; i++) {
            Map<String, String> map = new HashMap<>();
            map.put("openid", openId);
            JSONObject obj = byAccessToken(weixin, accessToken -> {
                map.put("access_token", weixin.getAccessToken());

                return http.get("https://api.weixin.qq.com/cgi-bin/user/info", null, map);
            });
            if (obj == null || obj.containsKey("errcode") || obj.size() <= 2)
                logger.warn(null, "获取微信[{}]用户[{}:{}]信息失败！", weixin.getKey(), map, obj);
            else if (!requireUnionId || obj.containsKey("unionid")) {
                object.putAll(obj);

                return false;
            }

            thread.sleep(1, TimeUnit.Second);
        }

        InfoModel info = infoService.find(openId);
        if (info != null && !validator.isEmpty(info.getUnionId()))
            object.put("unionid", info.getUnionId());

        return object.containsKey("unionid") || object.containsKey("unionId");
    }

    @Override
    public String subscribeQr(String key) {
        if (validator.isEmpty(synchUrl)) {
            WeixinModel weixin = weixinDao.findByKey(key);
            JSONObject object = new JSONObject();
            object.put("expire_seconds", 3600);
            object.put("action_name", "QR_STR_SCENE");
            JSONObject info = new JSONObject();
            info.put("scene_str", "sign-in-sid:" + session.getId());
            object.put("action_info", info);
            JSONObject obj = byAccessToken(weixin, accessToken -> http.post(
                    "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=" + accessToken,
                    null, object.toJSONString()));
            if (obj == null || !obj.containsKey("ticket") || !obj.containsKey("url")) {
                logger.warn(null, "获取微信关注二维码[{}:{}:{}]信息失败！", weixin.getAccessToken(), object, obj);

                return null;
            }

            cache.put(CACHE_TICKET_SESSION_ID + obj.getString("ticket"), session.getId(), false);

            return obj.getString("url");
        }

        Map<String, String> header = new HashMap<>();
        header.put(ServiceHelper.SESSION_ID, session.getId());
        Map<String, String> parameter = new HashMap<>();
        parameter.put("key", key);
        String string = http.post(synchUrl + "/weixin/subscribe-qr", header, parameter);
        JSONObject object = json.toObject(string);
        if (object == null || object.getIntValue("code") > 0) {
            logger.warn(null, "获取微信关注二维码[{}:{}:{}]信息失败！", synchUrl, key, string);

            return null;
        }

        return object.getString("data");
    }

    @Override
    public JSONObject subscribeSignIn() {
        JSONObject object = session.get(SESSION_SUBSCRIBE_SIGN_IN);
        if (object != null)
            return object;

        if (validator.isEmpty(synchUrl))
            return new JSONObject();

        Map<String, String> map = new HashMap<>();
        map.put(ServiceHelper.SESSION_ID, session.getId());
        String string = http.post(synchUrl + "/weixin/subscribe-sign-in", map, "");
        object = json.toObject(string);
        if (object == null || object.getIntValue("code") > 0) {
            logger.warn(null, "获取微信关注登入[{}:{}:{}]同步信息失败！", synchUrl, session.getId(), string);

            return new JSONObject();
        }

        JSONObject data = object.getJSONObject("data");
        if (!data.isEmpty())
            session.set(SESSION_SUBSCRIBE_SIGN_IN, data);

        return data;
    }

    @Override
    public String code(String key, String uri, String scope) {
        WeixinModel weixin = weixinDao.findByKey(key);

        return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + weixin.getAppId() + "&redirect_uri="
                + codec.encodeUrl(uri.contains("://") ? uri : ctrlHelper.url(uri), null)
                + "&response_type=code&scope=" + scope + "#wechat_redirect";
    }

    @Override
    public JSONObject auth(String key, String code) {
        if (code.equals("subscribe-sign-in")) {
            JSONObject object = session.get(SESSION_SUBSCRIBE_SIGN_IN);

            return object == null ? new JSONObject() : object;
        }

        WeixinModel weixin = weixinDao.findByKey(key);
        Map<String, String> map = getAuthMap(weixin);
        map.put("code", code);
        JSONObject object = json.toObject(http.get("https://api.weixin.qq.com/sns/oauth2/access_token", null, map));
        if (object == null || !object.containsKey("openid")) {
            logger.warn(null, "获取微信公众号用户认证信息[{}:{}:{}]失败！", key, code, object);

            return new JSONObject();
        }

        String openId = object.getString("openid");
        if (object.containsKey("access_token") && "snsapi_userinfo".equals(object.getString("scope"))) {
            map.clear();
            map.put("access_token", object.getString("access_token"));
            map.put("openid", openId);
            map.put("lang", "zh_CN");
            JSONObject obj = json.toObject(http.get("https://api.weixin.qq.com/sns/userinfo", null, map));
            if (obj != null)
                object.putAll(obj);
        }

        String nick = object.containsKey("nickname") ? object.getString("nickname") : null;
        if (nick == null)
            nick = object.containsKey("nickName") ? object.getString("nickName") : null;
        if (nick == null || nick.contains("微信")) {
            logger.info("未获取到有效的微信信息[{}:{}:{}]", key, code, object);

            return new JSONObject();
        }

        saveInfo(weixin, object, openId);
        if (logger.isInfoEnable())
            logger.info("获得微信公众号用户认证信息[{}:{}:{}]。", key, code, object);

        return object;
    }

    @Override
    public JSONObject auth(String key, String code, String iv, String message, String iv2, String message2) {
        WeixinModel weixin = weixinDao.findByKey(key);
        JSONObject object = code.equals("decrypt-iv-message") ? session.get(SESSION_MINI) : jscode2session(weixin, code);
        if (object.isEmpty())
            return object;

        String sessionKey = session.get(SESSION_MINI_SESSION_KEY);
        if (!validator.isEmpty(iv) && !validator.isEmpty(message))
            object.putAll(decryptAesCbcPkcs7(sessionKey, iv, message));
        if (!validator.isEmpty(iv2) && !validator.isEmpty(message2))
            object.putAll(decryptAesCbcPkcs7(sessionKey, iv2, message2));
        saveInfo(weixin, object, object.getString(object.containsKey("openid") ? "openid" : "openId"));
        if (logger.isDebugEnable())
            logger.debug("获得微信小程序用户认证信息[{}:{}:{}]。", key, code, object);

        return object;
    }

    private JSONObject jscode2session(WeixinModel weixin, String code) {
        Map<String, String> map = getAuthMap(weixin);
        map.put("js_code", code);
        String string = http.get("https://api.weixin.qq.com/sns/jscode2session", null, map);
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("openid")) {
            logger.warn(null, "获取微信小程序用户认证信息[{}:{}:{}]失败！", weixin.getKey(), map, string);

            return new JSONObject();
        }

        session.set(SESSION_MINI, object);
        session.set(SESSION_MINI_SESSION_KEY, object.getString("session_key"));
        object.remove("session_key");
        if (logger.isDebugEnable())
            logger.debug("获得微信小程序用户认证信息[{}:{}:{}]。", weixin.getKey(), map, object);

        return object;
    }

    private Map<String, String> getAuthMap(WeixinModel weixin) {
        Map<String, String> map = new HashMap<>();
        map.put("appid", weixin.getAppId());
        map.put("secret", weixin.getSecret());
        map.put("grant_type", "authorization_code");

        return map;
    }

    private void saveInfo(WeixinModel weixin, JSONObject object, String openId) {
        String unionId = infoService.save(weixin.getKey(), weixin.getAppId(),
                object.getString(object.containsKey("unionid") ? "unionid" : "unionId"), openId);
        if (!validator.isEmpty(unionId) && !object.containsKey("unionid"))
            object.put("unionid", unionId);
    }

    @Override
    public JSONObject getPhoneNumber(String key, String code) {
        WeixinModel weixin = weixinDao.findByKey(key);
        if (weixin == null)
            return null;

        JSONObject object = byAccessToken(weixin, accessToken -> http.post(
                "https://api.weixin.qq.com/wxa/business/getuserphonenumber?access_token=" + accessToken, null, "{\"code\": \"" + code + "\"}"));
        if (!json.containsKey(object, "phone_info"))
            return null;

        JSONObject phone = object.getJSONObject("phone_info");
        userService.mobile(phone.getString("phoneNumber"));

        return phone;
    }

    @Override
    public void prepayQrCode(String key, String user, String subject, int amount, String billNo, String notice, int size,
                             String logo, OutputStream outputStream) {
        Map<String, String> map = prepay(key, user, null, subject, amount, billNo, notice, "NATIVE");
        if (map == null)
            return;

        qrCode.create(map.get("code_url"), size > 0 ? size : qrCodeSize, getLogo(logo), outputStream);
    }

    @Override
    public String prepayQrCodeBase64(String key, String user, String subject, int amount, String billNo, String notice,
                                     int size, String logo) {
        Map<String, String> map = prepay(key, user, null, subject, amount, billNo, notice, "NATIVE");

        return map == null ? null : qrCode.create(map.get("code_url"), size > 0 ? size : qrCodeSize, getLogo(logo));
    }

    private String getLogo(String logo) {
        String path = validator.isEmpty(logo) ? qrCodeLogo : logo;

        return validator.isEmpty(path) ? null : storages.get(Storages.TYPE_DISK).getAbsolutePath(path);
    }

    @Override
    public JSONObject prepayApp(String key, String user, String subject, int amount, String billNo, String notice) {
        Map<String, String> map = prepay(key, user, null, subject, amount, billNo, notice, "APP");
        if (map == null)
            return null;

        Map<String, String> param = new HashMap<>();
        param.put("appid", map.get("appid"));
        param.put("partnerid", map.get("mch_id"));
        param.put("prepayid", map.get("prepay_id"));
        param.put("package", "Sign=WXPay");
        param.put("noncestr", generator.random(32));
        param.put("timestamp", numeric.toString(System.currentTimeMillis() / 1000, "0"));

        JSONObject object = new JSONObject();
        object.putAll(param);
        object.put("sign", sign(param, findByKey(key).getMchKey()));

        return object;
    }

    @Override
    public JSONObject prepayJsapi(String key, String user, String openId, String subject, int amount, String billNo, String notice) {
        Map<String, String> map = prepay(key, user, openId, subject, amount, billNo, notice, "JSAPI");
        if (map == null)
            return null;

        Map<String, String> param = new HashMap<>();
        param.put("appId", map.get("appid"));
        param.put("timeStamp", numeric.toString(System.currentTimeMillis() / 1000, "0"));
        param.put("nonceStr", generator.random(32));
        param.put("package", "prepay_id=" + map.get("prepay_id"));
        param.put("signType", "MD5");

        JSONObject object = new JSONObject();
        object.putAll(param);
        object.put("paySign", sign(param, findByKey(key).getMchKey()));

        return object;
    }

    @Override
    public String prepayH5(String key, String user, String subject, int amount, String billNo, String notice) {
        Map<String, String> map = prepay(key, user, null, subject, amount, billNo, notice, "MWEB");

        return map == null || !map.containsKey("mweb_url") ? null : map.get("mweb_url");
    }

    private Map<String, String> prepay(String key, String user, String openId, String subject, int amount, String billNo, String notice, String type) {
        String url = ctrlHelper.url("/weixin/notice");
        if (url == null) {
            logger.warn(null, "root未配置！");

            return null;
        }

        WeixinModel weixin = findByKey(key);
        if (weixin == null) {
            logger.warn(null, "获取微信配置[{}]失败！", key);

            return null;
        }

        Map<String, String> map = new HashMap<>();
        JSONObject object = paymentService.create("weixin", weixin.getAppId(), user, amount, billNo, notice, map);
        String orderNo;
        if (!json.containsKey(object, "orderNo") || validator.isEmpty(orderNo = object.getString("orderNo"))) {
            logger.warn(null, "创建支付订单[{}:{}:{}:{}:{}:{}]失败！", weixin.getAppId(), user, amount, billNo, notice, map);

            return null;
        }

        map.put("appid", weixin.getAppId());
        map.put("mch_id", weixin.getMchId());
        map.put("nonce_str", generator.random(32));
        map.put("body", subject);
        map.put("out_trade_no", orderNo);
        map.put("total_fee", numeric.toString(object.getIntValue("amount"), "0"));
        map.put("spbill_create_ip", header.getIp());
        map.put("notify_url", url);
        map.put("trade_type", type);
        if (type.equals("JSAPI"))
            map.put("openid", infoService.findOpenId(weixin.getAppId(), openId));
        map.put("sign", sign(map, weixin.getMchKey()));

        StringBuilder xml = new StringBuilder("<xml>");
        map.forEach((name, value) -> xml.append('<').append(name).append("><![CDATA[").append(value).append("]]></").append(name).append('>'));
        xml.append("</xml>");
        String html = http.post("https://api.mch.weixin.qq.com/pay/unifiedorder", null, xml.toString());
        if (validator.isEmpty(html)) {
            logger.warn(null, "微信预支付[{}:{}]失败！", xml, map);

            return null;
        }

        map = this.xml.toMap(html, false);
        if (!"SUCCESS".equals(map.get("return_code")) || !"SUCCESS".equals(map.get("result_code"))) {
            logger.warn(null, "微信预支付[{}:{}]失败！", xml, map);

            return null;
        }

        return map;
    }

    @Override
    public boolean notice(String appId, String orderNo, String tradeNo, String amount, String returnCode, String resultCode,
                          Map<String, String> map) {
        if (logger.isDebugEnable())
            logger.debug("微信支付结果回调[{}]。", converter.toString(map));

        WeixinModel weixin = findByAppId(appId);
        if (weixin == null)
            return false;

        if (!"SUCCESS".equals(map.get("return_code")) || !"SUCCESS".equals(map.get("result_code"))) {
            logger.warn(null, "微信支付回调[{}]返回失败！", converter.toString(map));

            return false;
        }

        if (!sign(map, weixin.getMchKey()).equals(map.get("sign"))) {
            logger.warn(null, "微信支付回调签名认证[{}]失败！", converter.toString(map));

            return false;
        }

        return json.has(paymentService.complete(orderNo, numeric.toInt(amount), tradeNo, 1, map), "orderNo", orderNo);
    }

    private String sign(Map<String, String> map, String mchKey) {
        List<String> list = new ArrayList<>(map.keySet());
        list.remove("sign");
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        list.forEach(key -> sb.append(key).append('=').append(map.get(key)).append('&'));
        sb.append("key=").append(mchKey);

        return digest.md5(sb.toString()).toUpperCase();
    }

    @Override
    public JSONObject decryptAesCbcPkcs7(String key, String code, String iv, String message) {
        if (!validator.isEmpty(key) && !validator.isEmpty(code))
            jscode2session(findByKey(key), code);
        String sessionKey = session.get(SESSION_MINI_SESSION_KEY);

        return validator.isEmpty(sessionKey) ? new JSONObject() : decryptAesCbcPkcs7(sessionKey, iv, message);
    }

    private JSONObject decryptAesCbcPkcs7(String sessionKey, String iv, String message) {
        try {
            if (Security.getProvider("BC") == null)
                Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(codec.decodeBase64(sessionKey), "AES"),
                    new IvParameterSpec(codec.decodeBase64(iv)));
            String string = new String(cipher.doFinal(codec.decodeBase64(message)));
            if (logger.isDebugEnable())
                logger.debug("获得decryptAesCbcPkcs7解密[{}:{}:{}]数据[{}]。", sessionKey, iv, message, string);

            return json.toObject(string, false);
        } catch (Exception e) {
            logger.warn(e, "解密微信数据[{}:{}:{}]时发生异常！", sessionKey, iv, message);

            return new JSONObject();
        }
    }

    @Override
    public String wxaCodeUnlimit(String key, String scene, String page, int width, boolean autoColor, JSONObject lineColor, boolean hyaline) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        JSONObject object = new JSONObject();
        object.put("scene", scene);
        object.put("page", page);
        object.put("width", width);
        object.put("auto_color", autoColor);
        object.put("line_color", lineColor);
        object.put("is_hyaline", hyaline);
        Map<String, String> responseHeaders = new HashMap<>();
        File file = new File(context.getAbsolutePath(temporary.newSavePath(".jpg")));
        try {
            OutputStream outputStream = new FileOutputStream(file);
            http.post("https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token="
                    + findByKey(key).getAccessToken(), headers, object.toJSONString(), null, responseHeaders, outputStream);
            outputStream.close();
            if ("image/jpeg".equals(responseHeaders.get("Content-Type"))) {
                String uri = wormholeHelper.image(null, null, null, file);
                if (validator.isEmpty(uri)) {
                    uri = uploadService.newSavePath("image/jpeg", "", ".jpg");
                    io.move(file, new File((context.getAbsolutePath(uri))));
                }

                return uri;
            } else {
                logger.warn(null, "获取微信二维码[{}:{}:{}]失败！", object, converter.toString(responseHeaders), io.readAsString(file.getAbsolutePath()));

                return "";
            }
        } catch (Throwable throwable) {
            logger.warn(throwable, "获取微信二维码[{}:{}:{}]时发生异常！", object,
                    converter.toString(responseHeaders), io.readAsString(file.getAbsolutePath()));

            return "";
        } finally {
            io.delete(file);
        }
    }

    @Override
    public JSONObject jsapiTicketSignature(String key, JSONObject param) {
        if (validator.isEmpty(param))
            return null;

        WeixinModel weixin = findByKey(key);
        param.put("noncestr", generator.random(32));
        param.put("jsapi_ticket", weixin.getJsapiTicket());
        param.put("timestamp", System.currentTimeMillis() / 1000);
        List<String> list = new ArrayList<>(param.keySet());
        Collections.sort(list);
        StringBuilder sb = new StringBuilder();
        list.forEach(k -> sb.append('&').append(k).append('=').append(param.get(k)));
        param.remove("jsapi_ticket");
        param.put("signature", digest.sha1(sb.deleteCharAt(0).toString()));
        param.put("appid", weixin.getAppId());

        return param;
    }

    @Override
    public JSONObject sendTemplateMessage(String key, String receiver, String templateId, String url, String miniAppId, String miniPagePath,
                                          JSONObject data, String color) {
        WeixinModel weixin = findByKey(key);
        if (weixin == null)
            return new JSONObject();

        String openId = infoService.findOpenId(weixin.getAppId(), receiver);
        if (openId == null) {
            logger.warn(null, "无法获得接收微信模板消息的Open ID[{}:{}]。", key, receiver);

            return new JSONObject();
        }

        JSONObject object = new JSONObject();
        object.put("touser", openId);
        object.put("template_id", templateId);
        if (!validator.isEmpty(url))
            object.put("url", url);
        if (!validator.isEmpty(miniAppId)) {
            JSONObject miniprogram = new JSONObject();
            miniprogram.put("appid", miniAppId);
            miniprogram.put("pagepath", miniPagePath);
            object.put("miniprogram", miniprogram);
        }
        object.put("data", data);
        if (!validator.isEmpty(color))
            object.put("color", color);

        return byAccessToken(weixin, accessToken -> {
            String string = http.post("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token="
                    + accessToken, null, object.toJSONString());
            if (logger.isInfoEnable())
                logger.info("发送微信模板消息[{}:{}]。", object.toJSONString(), string);

            return string;
        });
    }

    @Override
    public JSONObject sendMiniTemplateMessage(String key, String receiver, String templateId, String page, String formId,
                                              JSONObject data, String keyword) {
        WeixinModel weixin = findByKey(key);
        if (weixin == null)
            return new JSONObject();

        String openId = infoService.findOpenId(weixin.getAppId(), receiver);
        if (openId == null) {
            logger.warn(null, "无法获得接收微信小程序模板消息的Open ID[{}:{}]。", key, receiver);

            return new JSONObject();
        }

        JSONObject object = new JSONObject();
        object.put("touser", openId);
        object.put("template_id", templateId);
        object.put("form_id", formId);
        if (!validator.isEmpty(page))
            object.put("page", page);
        object.put("data", data);
        if (!validator.isEmpty(keyword))
            object.put("keyword", keyword);

        return byAccessToken(weixin, accessToken -> {
            String string = http.post("https://api.weixin.qq.com/cgi-bin/message/wxopen/template/send?access_token="
                    + accessToken, null, object.toJSONString());
            if (logger.isInfoEnable())
                logger.info("发送微信小程序模板消息[{}:{}]。", object.toJSONString(), string);

            return string;
        });
    }

    @Override
    public JSONObject sendTextMessage(String key, String openId, String message) {
        WeixinModel weixin = findByKey(key);
        if (weixin == null)
            return null;

        JSONObject object = new JSONObject();
        object.put("touser", openId);
        object.put("msgtype", "text");
        JSONObject text = new JSONObject();
        text.put("content", message);
        object.put("text", text);

        return byAccessToken(weixin, accessToken -> {
            String string = http.post("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + accessToken,
                    null, object.toJSONString());
            if (logger.isInfoEnable())
                logger.info("发送微信文本消息[{}:{}]。", object.toJSONString(), string);

            return string;
        });
    }

    @Override
    public JSONObject byAccessToken(WeixinModel weixin, Function<String, String> function) {
        String string = function.apply(weixin.getAccessToken());
        JSONObject object = json.toObject(string);
        if (object == null) {
            logger.warn(null, "获取微信Access Token信息[{}:{}:{}]失败！", weixin.getKey(), weixin.getAppId(), string);

            return null;
        }

        if (object.containsKey("errcode") && object.getIntValue("errcode") == 42001) {
            refreshAccessToken(weixin);
            string = function.apply(weixin.getAccessToken());
            object = json.toObject(string);
            if (object == null) {
                logger.warn(null, "获取微信Access Token信息[{}:{}:{}]失败！", weixin.getKey(), weixin.getAppId(), string);

                return null;
            }
        }

        return object;
    }

    @Override
    public int getContextRefreshedSort() {
        return 24;
    }

    @Override
    public void onContextRefreshed() {
        executeHourJob();
    }

    @Override
    public void executeHourJob() {
        if (!auto || !validator.isEmpty(synchUrl))
            return;

        String lockId = lockHelper.lock(WeixinModel.NAME + ".hour", 100, 60);
        if (lockId == null)
            return;

        List<WeixinModel> list = weixinDao.query().getList();
        weixinDao.close();
        list.forEach(this::refreshAccessToken);
        lockHelper.unlock(lockId);
    }

    private void refreshAccessToken(WeixinModel weixin) {
        for (int i = 0; i < 5; i++) {
            try {
                if (refreshAccessTokenOnce(weixin))
                    break;
            } catch (Throwable throwable) {
                logger.warn(throwable, "更新微信Access Token时发生异常！", modelHelper.toJson(weixin));
            }
        }
    }

    private boolean refreshAccessTokenOnce(WeixinModel weixin) {
        Map<String, String> map = new HashMap<>();
        map.put("grant_type", "client_credential");
        map.put("appid", weixin.getAppId());
        map.put("secret", weixin.getSecret());
        String string = http.get("https://api.weixin.qq.com/cgi-bin/token", null, map);
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("access_token")) {
            logger.warn(null, "获取微信公众号[{}]Token[{}]失败！", map, string);

            return false;
        }

        weixin.setAccessToken(object.getString("access_token"));
        map.clear();
        map.put("type", "jsapi");
        map.put("access_token", weixin.getAccessToken());
        string = http.get("https://api.weixin.qq.com/cgi-bin/ticket/getticket", null, map);
        object = json.toObject(string);
        if (object != null && object.containsKey("ticket"))
            weixin.setJsapiTicket(object.getString("ticket"));
        else
            logger.warn(null, "获取微信公众号JSAPI Ticket[{}:{}]失败！", modelHelper.toJson(weixin), object);

        weixin.setTime(dateTime.now());
        weixinDao.save(weixin);
        weixinDao.close();

        if (logger.isInfoEnable())
            logger.info("更新微信公众号[{}]Access Token[{}]与Jsapi Ticket[{}]。",
                    weixin.getAppId(), weixin.getAccessToken(), weixin.getJsapiTicket());

        return true;
    }

    @Override
    public JSONObject sync(String uri, Map<String, String> parameter) {
        if (!auto || validator.isEmpty(synchUrl))
            return null;

        sign.put(parameter, synchKey);
        String string = http.get(synchUrl + uri, null, parameter);
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("data")) {
            logger.warn(null, "获取微信[{}:{}:{}]同步数据[{}]失败！", synchUrl, uri, parameter, string);

            return null;
        }

        return object;
    }

    @Override
    public void executeMinuteJob() {
        JSONObject object = sync("/weixin/query", new HashMap<>());
        if (object == null)
            return;

        JSONArray array = object.getJSONArray("data");
        if (validator.isEmpty(array))
            return;

        String lockId = lockHelper.lock(WeixinModel.NAME + ".hour", 100, 60);
        if (lockId == null)
            return;

        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject obj = array.getJSONObject(i);
            WeixinModel weixin = weixinDao.findByKey(obj.getString("key"));
            if (weixin == null) {
                weixin = new WeixinModel();
                weixin.setKey(obj.getString("key"));
            }
            weixin.setName(obj.getString("name"));
            weixin.setAppId(obj.getString("appId"));
            weixin.setSecret(obj.getString("secret"));
            weixin.setToken(obj.getString("token"));
            weixin.setMchId(obj.getString("mchId"));
            weixin.setMchKey(obj.getString("mchKey"));
            weixin.setAccessToken(obj.getString("accessToken"));
            weixin.setJsapiTicket(obj.getString("jsapiTicket"));
            weixin.setMenu(obj.getString("menu"));
            weixin.setTime(dateTime.now());
            weixinDao.save(weixin);
        }
        lockHelper.unlock(lockId);
    }
}
