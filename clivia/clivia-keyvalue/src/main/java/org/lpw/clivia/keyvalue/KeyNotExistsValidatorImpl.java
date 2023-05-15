package org.lpw.clivia.keyvalue;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(KeyvalueService.VALIDATOR_KEY_NOT_EXISTS)
public class KeyNotExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private KeyvalueDao keyvalueDao;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        KeyvalueModel keyvalue = keyvalueDao.findByKey(parameters[1]);

        return keyvalue == null || keyvalue.getId().equals(parameters[0]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return KeyvalueModel.NAME + ".key.exists";
    }
}
