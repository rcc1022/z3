package org.lpw.photon.script;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ScriptService.VALIDATOR_EXISTS_METHOD)
public class ExistsMethodValidatorImpl extends ValidatorSupport {
    @Inject
    private Engine engine;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return engine.existsMethod();
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return "photon.script.method.not-exists";
    }
}
