package org.lpw.clivia.push;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(PushModel.NAME + ".ctrl")
@Execute(name = "/push/", key = PushModel.NAME, code = "108")
public class PushCtrl {
    @Inject
    private Request request;
    @Inject
    private PushService pushService;

    @Execute(name = "query", validates = {@Validate(validator = Validators.SIGN)})
    public Object query() {
        return pushService.query(request.get("scene"), request.get("sender"), request.get("name"),
                request.getAsInt("state", -1));
    }

    @Execute(name = "scenes", validates = {@Validate(validator = Validators.SIGN)})
    public Object scenes() {
        return pushService.scenes();
    }

    @Execute(name = "senders", validates = {@Validate(validator = Validators.SIGN)})
    public Object lvs() {
        return pushService.senders();
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "scene", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "scene", failureCode = 3),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "sender", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "sender", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 6),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "config", failureCode = 7),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 8),
            @Validate(validator = Validators.SIGN)})
    public Object save() {
        pushService.save(request.setToModel(PushModel.class));

        return "";
    }

    @Execute(name = "state", validates = {@Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 8),
            @Validate(validator = Validators.SIGN)})
    public Object state() {
        pushService.state(request.get("id"), request.getAsInt("state"));

        return "";
    }

    @Execute(name = "delete", validates = {@Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)})
    public Object delete() {
        pushService.delete(request.get("id"));

        return "";
    }
}
