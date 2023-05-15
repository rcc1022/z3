package org.lpw.clivia.ad;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(AdService.VALIDATOR_EXISTS)
public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private AdDao adDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return adDao.findById(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return AdModel.NAME + ".not-exists";
    }
}
