package org.lpw.clivia.weixin.template;

import org.lpw.clivia.weixin.WeixinService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(TemplateModel.NAME + ".ctrl")
@Execute(name = "/weixin/template/", key = TemplateModel.NAME, code = "155")
public class TemplateCtrl {
    @Inject
    private Request request;
    @Inject
    private TemplateService templateService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return templateService.query(request.get("key"), request.get("weixinKey"), request.getAsInt("type", -1),
                request.get("templateId"), request.getAsInt("state", -1));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 61),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 62),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "weixinKey", failureCode = 63),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "weixinKey", failureCode = 64),
            @Validate(validator = Validators.IN, number = {0, 1}, parameter = "type", failureCode = 65),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "templateId", failureCode = 66),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "templateId", failureCode = 67),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "page", failureCode = 69),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "miniAppId", failureCode = 70),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "color", failureCode = 71),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "keyword", failureCode = 72),
            @Validate(validator = Validators.IN, number = {0, 1}, parameter = "state", failureCode = 73),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "weixinKey", failureCode = 74)
    })
    public Object save() {
        templateService.save(request.setToModel(TemplateModel.class));

        return "";
    }

    @Execute(name = "send", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 61),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "receiver", failureCode = 75),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = TemplateService.VALIDATOR_EXISTS, parameter = "key", failureCode = 76)
    })
    public Object send() {
        return templateService.send(request.get("key"), request.get("receiver"), request.get("formId"),
                request.getAsJsonObject("data"), request.getAsJsonObject("args"));
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        templateService.delete(request.get("id"));

        return "";
    }
}
