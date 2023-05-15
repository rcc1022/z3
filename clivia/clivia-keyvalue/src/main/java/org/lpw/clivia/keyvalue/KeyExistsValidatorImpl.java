package org.lpw.clivia.keyvalue;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(KeyvalueService.VALIDATOR_KEY_EXISTS)
public class KeyExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private KeyvalueDao keyvalueDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return keyvalueDao.findByKey(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return KeyvalueModel.NAME + ".key.not-exists";
    }
}
