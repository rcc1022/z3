package org.lpw.clivia.account.log;

import javax.inject.Inject;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

@Controller(LogModel.NAME + ".ctrl")
@Execute(name = "/account/log/", key = LogModel.NAME, code = "152")
public class LogCtrl {
    @Inject
    private Request request;
    @Inject
    private LogService logService;

    @Execute(name = "query", validates = { @Validate(validator = Validators.SIGN) })
    public Object query() {
        return logService.query(request.get("uid"), request.get("owner"), request.get("type"), request.get("channel"),
                request.getAsInt("state", -1), request.get("start"));
    }

    @Execute(name = "pass", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "ids", failureCode = 21),
            @Validate(validator = Validators.SIGN) })
    public Object pass() {
        return logService.pass(request.getAsArray("ids"));
    }

    @Execute(name = "reject", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "ids", failureCode = 21),
            @Validate(validator = Validators.SIGN) })
    public Object reject() {
        return logService.reject(request.getAsArray("ids"));
    }
}
