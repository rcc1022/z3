package org.lpw.clivia.category;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(CategoryService.VALIDATOR_EXISTS)
public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private CategoryDao categoryDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return categoryDao.findById(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return CategoryModel.NAME + ".not-exists";
    }
}
