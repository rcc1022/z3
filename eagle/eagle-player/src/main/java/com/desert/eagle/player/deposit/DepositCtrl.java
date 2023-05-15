package com.desert.eagle.player.deposit;

import com.desert.eagle.player.PlayerService;
import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(DepositModel.NAME + ".ctrl")
@Execute(name = "/player/deposit/", key = DepositModel.NAME, code = "201")
public class DepositCtrl {
    @Inject
    private Request request;
    @Inject
    private DepositService depositService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return depositService.query(request.get("uid"), request.get("nick"), request.getAsInt("status", -1),
                request.get("time"), request.getAsInt("timeN", -1));
    }

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return depositService.user();
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.SIGN),
            @Validate(validator = PlayerService.VALIDATOR_UID_EXISTS, parameter = "uid", failureCode = 1)
    })
    public Object save() {
        depositService.save(request.get("uid"), request.getAsInt("amount2"));

        return "";
    }

    @Execute(name = "submit", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object submit() {
        return depositService.submit(request.get("type"), request.getAsInt("amount") * 100);
    }

    @Execute(name = "pass", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object pass() {
        depositService.pass(request.get("id"));

        return "";
    }

    @Execute(name = "reject", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object reject() {
        depositService.reject(request.get("id"));

        return "";
    }

    @Execute(name = "newer", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object newer() {
        return depositService.newer();
    }
}