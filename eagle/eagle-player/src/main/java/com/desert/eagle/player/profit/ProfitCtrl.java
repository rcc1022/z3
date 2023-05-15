package com.desert.eagle.player.profit;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ProfitModel.NAME + ".ctrl")
@Execute(name = "/player/profit/", key = ProfitModel.NAME, code = "201")
public class ProfitCtrl {
    @Inject
    private Request request;
    @Inject
    private ProfitService profitService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return profitService.query(request.get("uid"), request.get("nick"), request.get("invitor"), request.get("date"));
    }

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return profitService.user();
    }

    @Execute(name = "junior", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object junior() {
        return profitService.junior(request.get("player"));
    }

    @Execute(name = "water", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object water() {
        return profitService.today();
    }

    @Execute(name = "towater", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object towater() {
        return profitService.towater();
    }

    @Execute(name = "pass-all", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object passAll() {
        profitService.passAll();

        return "";
    }

    @Execute(name = "pass", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object pass() {
        profitService.pass(request.get("id"));

        return "";
    }

    @Execute(name = "reject", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object reject() {
        profitService.reject(request.get("id"));

        return "";
    }
}