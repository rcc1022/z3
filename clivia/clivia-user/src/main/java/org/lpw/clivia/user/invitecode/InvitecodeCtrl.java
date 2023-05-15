package org.lpw.clivia.user.invitecode;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(InvitecodeModel.NAME + ".ctrl")
@Execute(name = "/user/invitecode/", key = InvitecodeModel.NAME, code = "151")
public class InvitecodeCtrl {
    @Inject
    private Request request;
    @Inject
    private InvitecodeService invitecodeService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return invitecodeService.query(request.get("batch"));
    }

    @Execute(name = "generate", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "batch", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "length", failureCode = 4),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "count", failureCode = 5),
            @Validate(validator = Validators.SIGN)
    })
    public Object generate() {
        invitecodeService.generate(request.get("batch"), request.getAsInt("length"), request.getAsInt("count"));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        invitecodeService.delete(request.get("id"));

        return "";
    }
}