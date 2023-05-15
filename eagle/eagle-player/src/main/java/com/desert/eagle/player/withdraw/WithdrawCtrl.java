package com.desert.eagle.player.withdraw;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(WithdrawModel.NAME + ".ctrl")
@Execute(name = "/player/withdraw/", key = WithdrawModel.NAME, code = "201")
public class WithdrawCtrl {
    @Inject
    private Request request;
    @Inject
    private WithdrawService withdrawService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return withdrawService.query(request.get("uid"), request.getAsInt("type", -1), request.getAsInt("status", -1),
                request.get("time"), request.getAsInt("timeN", -1));
    }

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return withdrawService.user();
    }

    @Execute(name = "surplus", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object surplus() {
        return withdrawService.surplus();
    }

    @Execute(name = "submit", permit = Permit.sign, validates = {
            @Validate(validator = WithdrawService.TIME_RANGE_VALIDATOR, parameter = "type", failureCode = 1),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object submit() {
        return withdrawService.submit(request.getAsInt("type"), request.getAsInt("amount"));
    }

    @Execute(name = "pass", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object pass() {
        withdrawService.pass(request.get("id"));

        return "";
    }

    @Execute(name = "reject", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object reject() {
        withdrawService.reject(request.get("id"));

        return "";
    }

    @Execute(name = "newer", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object newer() {
        return withdrawService.newer();
    }
}