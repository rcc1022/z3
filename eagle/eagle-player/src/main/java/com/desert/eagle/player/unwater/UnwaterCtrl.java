package com.desert.eagle.player.unwater;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UnwaterModel.NAME + ".ctrl")
@Execute(name = "/player/unwater/", key = UnwaterModel.NAME, code = "201")
public class UnwaterCtrl {
    @Inject
    private Request request;
    @Inject
    private UnwaterService unwaterService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return unwaterService.query(request.get("time"));
    }
}