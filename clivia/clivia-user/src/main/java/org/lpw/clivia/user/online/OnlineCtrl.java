package org.lpw.clivia.user.online;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(OnlineModel.NAME + ".ctrl")
@Execute(name = "/user/online/", key = OnlineModel.NAME, code = "151")
public class OnlineCtrl {
    @Inject
    private Request request;
    @Inject
    private OnlineService onlineService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return onlineService.query(request.get("uid"), request.get("ip"));
    }

    @Execute(name = "sign-out", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object signOut() {
        onlineService.signOutId(request.get("id"));

        return "";
    }
}
