package org.lpw.clivia.weixin.qrcode;

import org.lpw.clivia.weixin.WeixinService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(QrcodeModel.NAME + ".ctrl")
@Execute(name = "/weixin/qrcode/", key = QrcodeModel.NAME, code = "155")
public class QrcodeCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private QrcodeService qrcodeService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return qrcodeService.query(request.get("key"), request.get("appId"), request.get("user"), request.get("name"), request.get("scene"), request.get("time"));
    }

    @Execute(name = "find", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object find() {
        return qrcodeService.find(request.get("key"), request.get("user"), request.get("name"));
    }

    @Execute(name = "create", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.ID, parameter = "user", failureCode = 91),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 92),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 93),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "scene", failureCode = 94),
            @Validate(validator = Validators.MAX_LENGTH, number = {64}, parameter = "scene", failureCode = 95),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object create() {
        QrcodeModel qrcode = qrcodeService.create(request.get("key"), request.get("user"), request.get("name"), request.get("scene"));

        return qrcode == null ? templates.get().failure(2496, message.get(QrcodeModel.NAME + ".create.failure"),
                null, null) : "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        qrcodeService.delete(request.get("id"));

        return "";
    }
}
