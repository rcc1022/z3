package com.desert.eagle.player.commission;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(CommissionModel.NAME + ".ctrl")
@Execute(name = "/player/commission/", key = CommissionModel.NAME, code = "201")
public class CommissionCtrl {
    @Inject
    private Request request;
    @Inject
    private CommissionService commissionService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return commissionService.query();
    }

    @Execute(name = "pass", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object pass() {
        commissionService.pass(request.get("id"));

        return "";
    }

    @Execute(name = "reject", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object reject() {
        commissionService.reject(request.get("id"));

        return "";
    }
}