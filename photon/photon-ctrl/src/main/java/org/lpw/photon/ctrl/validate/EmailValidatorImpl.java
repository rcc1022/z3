package org.lpw.photon.ctrl.validate;

import org.springframework.stereotype.Controller;

@Controller(Validators.EMAIL)
public class EmailValidatorImpl extends ValidatorSupport {
    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return validator.isEmail(parameter);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-email";
    }
}
