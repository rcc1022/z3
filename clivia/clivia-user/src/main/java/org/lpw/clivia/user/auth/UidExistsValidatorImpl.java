package org.lpw.clivia.user.auth;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(AuthService.VALIDATOR_UID_EXISTS)
public class UidExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private AuthService authService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return authService.findByUid(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return AuthModel.NAME + ".uid.not-exists";
    }
}
