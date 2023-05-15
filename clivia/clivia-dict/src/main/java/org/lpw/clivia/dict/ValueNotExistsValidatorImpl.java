package org.lpw.clivia.dict;

import javax.inject.Inject;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

@Controller(DictService.VALIDATOR_VALUE_NOT_EXISTS)
public class ValueNotExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private DictDao dictDao;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        DictModel dict = dictDao.findByKeyValue(parameters[1], parameters[2]);

        return dict == null || dict.getId().equals(parameters[0]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return DictModel.NAME + ".value.exists";
    }
}
