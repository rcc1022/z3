package org.lpw.clivia.wps;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(WpsModel.NAME + ".ctrl")
@Execute(name = "/wps/", key = WpsModel.NAME, code = "157")
public class WpsCtrl {
    @Inject
    private Request request;
    @Inject
    private WpsService wpsService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return wpsService.query(request.get("key"), request.get("name"), request.get("appId"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "appId", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "appId", failureCode = 5),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "secret", failureCode = 6),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "secret", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "watermark", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 9),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "avatar", failureCode = 10),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = WpsService.VALIDATOR_KEY_NOT_EXISTS, parameters = {"id", "key"}, failureCode = 11)
    })
    public Object save() {
        wpsService.save(request.setToModel(WpsModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        wpsService.delete(request.get("id"));

        return "";
    }
}
