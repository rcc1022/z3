package com.desert.eagle.daily;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(DailyModel.NAME + ".ctrl")
@Execute(name = "/daily/", key = DailyModel.NAME, code = "241")
public class DailyCtrl {
    @Inject
    private Request request;
    @Inject
    private DailyService dailyService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return dailyService.query(request.get("game"), request.get("date"));
    }

    @Execute(name = "all", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object all() {
        return dailyService.query(null, null);
    }

    @Execute(name = "today", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object today() {
        return dailyService.today(request.getAsBoolean("shortcut"));
    }

    @Execute(name = "day7", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object day7() {
        return dailyService.day7();
    }
}