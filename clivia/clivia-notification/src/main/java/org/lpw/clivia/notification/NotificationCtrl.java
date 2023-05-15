package org.lpw.clivia.notification;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(NotificationModel.NAME + ".ctrl")
@Execute(name = "/notification/", key = NotificationModel.NAME, code = "163")
public class NotificationCtrl {
    @Inject
    private Request request;
    @Inject
    private NotificationService notificationService;

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return notificationService.user(request.get("genre"));
    }

    @Execute(name = "unread", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object unread() {
        return notificationService.unread(request.getAsArray("genre"));
    }
}