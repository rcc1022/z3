package org.lpw.clivia.popular;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(PopularService.VALIDATOR_EXISTS)
public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private PopularDao popularDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return popularDao.findById(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return PopularModel.NAME + ".not-exists";
    }
}
