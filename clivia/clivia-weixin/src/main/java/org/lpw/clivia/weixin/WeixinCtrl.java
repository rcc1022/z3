package org.lpw.clivia.weixin;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.context.Response;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Validator;
import org.lpw.photon.util.Xml;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.Map;

@Controller(WeixinModel.NAME + ".ctrl")
@Execute(name = "/weixin/", key = WeixinModel.NAME, code = "155")
public class WeixinCtrl {
    @Inject
    private Validator validator;
    @Inject
    private Xml xml;
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Response response;
    @Inject
    private Templates templates;
    @Inject
    private WeixinService weixinService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"})
    })
    public Object query() {
        return weixinService.query();
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 4),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "appId", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "appId", failureCode = 6),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "secret", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "secret", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "token", failureCode = 9),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchId", failureCode = 10),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchPartnerId", failureCode = 10),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchKey", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchSerialNo", failureCode = 10),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchKeyV3", failureCode = 11),
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"}),
            @Validate(validator = WeixinService.VALIDATOR_NOT_EXISTS, parameters = {"key", "appId"}, failureCode = 12)
    })
    public Object save() {
        return weixinService.save(request.setToModel(WeixinModel.class));
    }

    @Execute(name = "single", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object single() {
        return weixinService.single();
    }

    @Execute(name = "single-save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "appId", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "appId", failureCode = 6),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "secret", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "secret", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "token", failureCode = 9),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchId", failureCode = 10),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchPartnerId", failureCode = 10),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchKey", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchSerialNo", failureCode = 10),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "mchKeyV3", failureCode = 11),
            @Validate(validator = Validators.SIGN)
    })
    public Object singleSave() {
        weixinService.singleSave(request.setToModel(WeixinModel.class));

        return "";
    }

    @Execute(name = "refresh-access-token", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object refreshAccessToken() {
        weixinService.refreshAccessToken(request.get("key"));

        return "";
    }

    @Execute(name = "menu", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object menu() {
        return weixinService.menu(request.get("key"));
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"})
    })
    public Object delete() {
        weixinService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "wx.+", regex = true, permit = Permit.always, type = Templates.STRING)
    public Object service() {
        String uri = request.getUri();
        String appId = uri.substring(uri.lastIndexOf('/') + 1);
        String echostr = request.get("echostr");
        if (!validator.isEmpty(echostr))
            return weixinService.echo(appId, request.get("signature"), request.get("timestamp"), request.get("nonce"), echostr);

        weixinService.notice(appId, request.getFromInputStream());

        return "";
    }

    @Execute(name = "subscribe-qr", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object subscribeQr() {
        String qr = weixinService.subscribeQr(request.get("key"));

        return qr == null ? templates.get().failure(2453, message.get(WeixinModel.NAME + ".subscribe-qr.failure"),
                null, null) : qr;
    }

    @Execute(name = "subscribe-sign-in")
    public Object subscribeSignIn() {
        return weixinService.subscribeSignIn();
    }

    @Execute(name = "app-id", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object appId() {
        return weixinService.findByKey(request.get("key")).getAppId();
    }

    @Execute(name = "code", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uri", failureCode = 25),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "scope", failureCode = 26),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object authorize() {
        return weixinService.code(request.get("key"), request.get("uri"), request.get("scope"));
    }

    @Execute(name = "auth", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "code", failureCode = 51),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object auth() {
        return weixinService.auth(request.get("key"), request.get("code"));
    }

    @Execute(name = "auth-mini", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "code", failureCode = 51),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object authMini() {
        return weixinService.auth(request.get("key"), request.get("code"), request.get("iv"), request.get("message"),
                request.get("iv2"), request.get("message2"));
    }

    @Execute(name = "prepay-qr-code", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "subject", failureCode = 21),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 22),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 23),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 24)
    })
    public Object prepayQrCode() {
        weixinService.prepayQrCode(request.get("key"), request.get("user"), request.get("subject"),
                request.getAsInt("amount"), request.get("billNo"), request.get("notice"),
                request.getAsInt("size"), request.get("logo"), response.getOutputStream());

        return null;
    }

    @Execute(name = "prepay-qr-code-base64", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "subject", failureCode = 21),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 22),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 23),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 24)
    })
    public Object prepayQrCodeBase64() {
        return weixinService.prepayQrCodeBase64(request.get("key"), request.get("user"), request.get("subject"),
                request.getAsInt("amount"), request.get("billNo"), request.get("notice"), request.getAsInt("size"),
                request.get("logo"));
    }

    @Execute(name = "prepay-app", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "subject", failureCode = 21),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 22),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 23),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 24)
    })
    public Object prepayApp() {
        return prepay(weixinService.prepayApp(request.get("key"), request.get("user"), request.get("subject"),
                request.getAsInt("amount"), request.get("billNo"), request.get("notice")));
    }

    @Execute(name = "prepay-jsapi,prepay-mini", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "subject", failureCode = 21),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 22),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 23),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 24)
    })
    public Object prepayJsapi() {
        return prepay(weixinService.prepayJsapi(request.get("key"), request.get("user"), request.get("openId"), request.get("subject"),
                request.getAsInt("amount"), request.get("billNo"), request.get("notice")));
    }

    @Execute(name = "prepay-h5", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "subject", failureCode = 21),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 22),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 23),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 24)
    })
    public Object prepayH5() {
        return prepay(weixinService.prepayH5(request.get("key"), request.get("user"), request.get("subject"),
                request.getAsInt("amount"), request.get("billNo"), request.get("notice")));
    }

    private Object prepay(Object object) {
        return object == null ? templates.get().failure(2427, message.get(WeixinModel.NAME + ".prepay.failure"),
                null, null) : object;
    }

    @Execute(name = "notice", permit = Permit.always, type = Templates.STRING)
    public Object notice() {
        Map<String, String> map = xml.toMap(request.getFromInputStream(), false);
        String code = weixinService.notice(map.get("appid"), map.get("out_trade_no"), map.get("transaction_id"),
                map.get("total_fee"), map.get("return_code"), map.get("result_code"), map) ? "SUCCESS" : "FAIL";

        return "<xml><return_code><![CDATA[" + code + "]]></return_code></xml>";
    }

    @Execute(name = "decrypt-aes-cbc-pkcs7", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "iv", failureCode = 33),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "message", failureCode = 34),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, emptyable = true, parameter = "key", failureCode = 24)
    })
    public Object decryptAesCbcPkcs7() {
        return weixinService.decryptAesCbcPkcs7(request.get("key"), request.get("code"), request.get("iv"), request.get("message"));
    }

    @Execute(name = "wxa-code-unlimit", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 24)
    })
    public Object wxaCodeUnlimit() {
        String string = weixinService.wxaCodeUnlimit(request.get("key"), request.get("scene"), request.get("page"),
                request.getAsInt("width"), request.getAsBoolean("autoColor"),
                request.getAsJsonObject("lineColor"), request.getAsBoolean("hyaline"));

        return string.charAt(0) == '{' ? templates.get().failure(2428,
                message.get(WeixinModel.NAME + ".wxa-code-unlimit.failure", string), null, null) : string;
    }

    @Execute(name = "jsapi-ticket-signature", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 24)
    })
    public Object jsapiTicketSignature() {
        JSONObject object = weixinService.jsapiTicketSignature(request.get("key"), request.getAsJsonObject("param"));

        return object == null ? templates.get().failure(2429, message.get(WeixinModel.NAME + ".param.not-json"),
                "param", request.get("param")) : object;
    }

    @Execute(name = "send-template-message", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "receiver", failureCode = 30),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "templateId", failureCode = 31),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "data", failureCode = 32),
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"}),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 24)
    })
    public Object sendTemplateMessage() {
        return weixinService.sendTemplateMessage(request.get("key"), request.get("receiver"), request.get("templateId"),
                request.get("url"), request.get("miniAppId"), request.get("miniPagePath"),
                request.getAsJsonObject("data"), request.get("color"));
    }
}
