package org.lpw.clivia.user;

import org.lpw.clivia.user.type.Types;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserService.VALIDATOR_EXISTS_TYPE)
public class ExistsTypeValidatorImpl extends ValidatorSupport {
    @Inject
    private Types types;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return types.hasKey(parameter);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return UserModel.NAME + ".type.not-exists";
    }
}
