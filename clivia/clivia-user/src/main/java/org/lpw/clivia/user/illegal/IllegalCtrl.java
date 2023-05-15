package org.lpw.clivia.user.illegal;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(IllegalModel.NAME + ".ctrl")
@Execute(name = "/user/illegal/", key = IllegalModel.NAME, code = "151")
public class IllegalCtrl {
    @Inject
    private Request request;
    @Inject
    private IllegalService illegalService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return illegalService.query();
    }
}