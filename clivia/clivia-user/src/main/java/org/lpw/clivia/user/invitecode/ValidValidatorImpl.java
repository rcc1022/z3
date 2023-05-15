package org.lpw.clivia.user.invitecode;

import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(InvitecodeService.VALIDATOR_VALID)
public class ValidValidatorImpl extends ValidatorSupport {
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private InvitecodeDao invitecodeDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        if (keyvalueService.valueAsInt("setting.user.sign-up.invite-code", 0) != 1)
            return true;

        if (validator.isEmpty(parameter))
            return false;

        InvitecodeModel invitecode = invitecodeDao.find(parameter);

        return invitecode != null && invitecode.getUser() == null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return InvitecodeModel.NAME + ".illegal";
    }
}
