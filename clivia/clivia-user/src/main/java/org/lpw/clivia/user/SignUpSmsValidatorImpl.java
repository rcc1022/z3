package org.lpw.clivia.user;

import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.push.CaptchaValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserService.VALIDATOR_SIGN_UP_SMS)
public class SignUpSmsValidatorImpl extends CaptchaValidatorSupport {
    @Inject
    private KeyvalueService keyvalueService;

    @Override
    protected boolean ignore(String parameter) {
        return keyvalueService.valueAsInt("setting.user.sign-up.sms", 0) == 0;
    }
}
