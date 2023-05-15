package org.lpw.clivia.push.huaweiyun;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.push.PushModel;
import org.lpw.clivia.push.PushSender;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;

@Service("clivia.push.huaweiyun.sender.sms")
public class SmsPushSenderImpl implements PushSender {
    @Inject
    private Json json;
    @Inject
    private Generator generator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Digest digest;
    @Inject
    private Codec codec;
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Http http;
    @Inject
    private Logger logger;

    @Override
    public String key() {
        return "huaweiyun.sms";
    }

    @Override
    public String name() {
        return "clivia.push.huaweiyun.sms.name";
    }

    @Override
    public Object push(PushModel push, JSONObject config, JSONObject args) {
        String mobile = args.getString("mobile");
        String content = args.getString("content");
        if (!validator.isMobile(mobile) || config == null || !config.containsKey("url") || !config.containsKey("key")
                || !config.containsKey("secret") || !config.containsKey("from") || !config.containsKey("templateId")
                || !config.containsKey("signature")) {
            logger.warn(null, "华为云短信推送配置[{}]参数[{}]错误！", config, args);

            return null;
        }

        String nonce = generator.random(16);
        String time = dateTime.toString(dateTime.now(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
        String string = http.post(config.getString("url") + "/push/batchSendSms/v1",
                Map.of("Content-Type", "application/x-www-form-urlencoded", "Authorization",
                        "WSSE realm=\"SDP\",profile=\"UsernameToken\",type=\"Appkey\"", "X-WSSE",
                        String.format("UsernameToken Username=\"%s\",PasswordDigest=\"%s\",Nonce=\"%s\",Created=\"%s\"",
                                config.getString("key"),
                                codec.encodeBase64(
                                        digest.digest("sha256", (nonce + time + config.getString("secret")).getBytes())),
                                nonce, time)),
                Map.of("from", config.getString("from"), "to", mobile, "templateId", config.getString("templateId"),
                        "templateParas", paras(content), "signature", config.getString("signature")));
        if (logger.isInfoEnable())
            logger.info("发送华为云短信[{}:{}:{}:{}]。", config, mobile, content, string);
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("code"))
            return null;

        if (json.has(object, "code", "000000"))
            return "";

        JSONObject obj = new JSONObject();
        obj.put("code", 108999);
        obj.put("message", string);

        return obj;
    }

    private String paras(String content) {
        if (validator.isEmpty(content))
            return "";

        JSONArray array = new JSONArray();
        array.addAll(Arrays.asList(converter.toArray(content, ",")));

        return array.toString();
    }
}
