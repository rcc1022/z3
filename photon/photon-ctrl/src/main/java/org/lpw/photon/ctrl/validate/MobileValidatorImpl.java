package org.lpw.photon.ctrl.validate;

import org.springframework.stereotype.Controller;

@Controller(Validators.MOBILE)
public class MobileValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isMobile(parameter);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-mobile";
    }
}
