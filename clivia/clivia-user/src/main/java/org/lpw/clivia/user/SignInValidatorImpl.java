package org.lpw.clivia.user;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.lpw.photon.util.Numeric;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserService.VALIDATOR_SIGN_IN)
public class SignInValidatorImpl extends ValidatorSupport {
    @Inject
    private Numeric numeric;
    @Inject
    private UserService userService;
    private final ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        int n = userService.signIn(parameters[0], parameters[1], parameters[2], parameters[3]);
        if (n == 0)
            return true;

        threadLocal.set(n);

        return false;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        int n = numeric.toInt(threadLocal.get());

        return UserModel.NAME + (n == 2 ? ".state0" : ".sign-in.failure");
    }
}
