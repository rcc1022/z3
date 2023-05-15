package org.lpw.clivia.wps;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(WpsService.VALIDATOR_KEY_NOT_EXISTS)
public class KeyNotExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private WpsDao wpsDao;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        WpsModel wps = wpsDao.findByKey(parameters[1]);

        return wps == null || wps.getId().equals(parameters[0]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return WpsModel.NAME + ".key.exists";
    }
}
