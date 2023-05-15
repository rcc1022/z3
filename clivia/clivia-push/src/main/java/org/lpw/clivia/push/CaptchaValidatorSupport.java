package org.lpw.clivia.push;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;

import javax.inject.Inject;

public class CaptchaValidatorSupport extends ValidatorSupport {
    @Inject
    private PushService pushService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return ignore(parameter) || pushService.captcha(parameter);
    }

    protected boolean ignore(String parameter) {
        return false;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return PushModel.NAME + ".captcha.illegal";
    }
}
