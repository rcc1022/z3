package org.lpw.clivia.user.auth;

import javax.inject.Inject;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

@Controller(AuthModel.NAME + ".ctrl")
@Execute(name = "/user/auth/", key = AuthModel.NAME, code = "151")
public class AuthCtrl {
    @Inject
    private Request request;
    @Inject
    private AuthService authService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "user", failureCode = 51),
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return authService.query(request.get("user"));
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uid", failureCode = 1),
            @Validate(validator = UserService.VALIDATOR_PASSWORD, parameters = {"password", "type"}, failureCode = 3),
            @Validate(validator = UserService.VALIDATOR_EXISTS_TYPE, parameter = "type", failureCode = 27),
            @Validate(validator = UserService.VALIDATOR_SIGN),
            @Validate(validator = UserService.VALIDATOR_SIGN_IN, parameters = {"uid", "password", "type"}, failureCode = 6)
    })
    public Object delete() {
        authService.delete();

        return "";
    }
}
