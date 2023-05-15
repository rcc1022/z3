package com.desert.eagle.rate;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(RateModel.NAME + ".ctrl")
@Execute(name = "/rate/", key = RateModel.NAME, code = "208")
public class RateCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private RateService rateService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return rateService.query(request.get("game"), request.get("type"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.ID, parameter = "game", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "type", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "memo", failureCode = 6),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        rateService.save(request.setToModel(RateModel.class));

        return "";
    }

    @Execute(name = "reset", validates = {
            @Validate(validator = Validators.ID, parameter = "game", failureCode = 1, failureKey = RateModel.NAME + ".game.empty"),
            @Validate(validator = Validators.SIGN)
    })
    public Object reset() {
        rateService.reset(request.get("game"));

        return templates.get().success(null, message.get(RateModel.NAME + ".reset.success"));
    }

    @Execute(name = "sai-che", validates = {
            @Validate(validator = Validators.ID, parameter = "game", failureCode = 1, failureKey = RateModel.NAME + ".game.empty"),
            @Validate(validator = Validators.SIGN)
    })
    public Object saiChe() {
        rateService.saiChe(request.get("game"), request.getAsInt("haoMa"), request.getAsInt("shuangMian"));

        return "";
    }

    @Execute(name = "list", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object list() {
        return rateService.list(request.get("game"));
    }
}