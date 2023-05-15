package org.lpw.clivia.user.password;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(PasswordModel.NAME + ".ctrl")
@Execute(name = "/user/password/", key = PasswordModel.NAME, code = "151")
public class PasswordCtrl {
    @Inject
    private Request request;
    @Inject
    private PasswordService passwordService;

    @Execute(name = "has", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object has() {
        return passwordService.has();
    }

    @Execute(name = "auth", permit = Permit.sign, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 101),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "type", failureCode = 102),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "password", failureCode = 103),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object auth() {
        return passwordService.auth(null, request.get("type"), request.get("password"));
    }

    @Execute(name = "set", permit = Permit.sign, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 101),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "type", failureCode = 102),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "new", failureCode = 104),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object set() {
        return passwordService.set(null, request.get("type"), request.get("new"), request.get("old"), request.getAsBoolean("unique"));
    }

    @Execute(name = "off", permit = Permit.sign, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 101),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "type", failureCode = 102),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "password", failureCode = 103),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object off() {
        return passwordService.off(null, request.get("type"), request.get("password"));
    }
}