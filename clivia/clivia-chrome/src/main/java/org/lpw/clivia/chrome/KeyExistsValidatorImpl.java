package org.lpw.clivia.chrome;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ChromeService.VALIDATOR_KEY_EXISTS)
public class KeyExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private ChromeService chromeService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return chromeService.findByKey(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return ChromeModel.NAME + ".key.not-exists";
    }
}
