package org.lpw.clivia.user.auth;

import org.lpw.clivia.user.type.Types;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.Set;

@Controller(AuthService.VALIDATOR_UID_NOT_EXISTS)
public class UidNotExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private Types types;
    @Inject
    private AuthService authService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return authService.findByUid(parameter) == null;
    }

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        if (!Types.Self.equals(parameters[2]))
            return false;

        Set<String> uid = types.getUid(Types.Self, parameters[0], parameters[1]);

        return !validator.isEmpty(uid) && authService.findByUid(uid.iterator().next()) == null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return AuthModel.NAME + ".uid.exists";
    }
}
