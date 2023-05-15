package org.lpw.clivia.user;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserService.VALIDATOR_SIGN)
public class SignValidatorImpl extends ValidatorSupport {
    @Inject
    private UserService userService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return !userService.sign().isEmpty();
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 151901;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return UserModel.NAME + ".need-sign-in";
    }
}
