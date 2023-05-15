package org.lpw.clivia.group.member;

import javax.inject.Inject;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

@Controller(MemberService.VALIDATOR_EXISTS)
public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private MemberDao memberDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return memberDao.findById(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return MemberModel.NAME + ".not-exists";
    }
}
