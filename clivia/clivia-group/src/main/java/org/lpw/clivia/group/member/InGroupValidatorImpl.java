package org.lpw.clivia.group.member;

import javax.inject.Inject;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

@Controller(MemberService.VALIDATOR_IN_GROUP)
public class InGroupValidatorImpl extends ValidatorSupport {
    @Inject
    private UserService userService;
    @Inject
    private MemberDao memberDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return memberDao.find(parameter, userService.id()) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return MemberModel.NAME + ".not-in-group";
    }
}
