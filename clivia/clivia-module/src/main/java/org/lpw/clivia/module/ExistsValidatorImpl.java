package org.lpw.clivia.module;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ModuleService.VALIDATOR_EXISTS)
public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private ModuleDao moduleDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return moduleDao.findById(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return ModuleModel.NAME + ".not-exists";
    }
}
