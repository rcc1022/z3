package org.lpw.clivia.category;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(CategoryService.VALIDATOR_LEAF)
public class LeafValidatorImpl extends ValidatorSupport {
    @Inject
    private CategoryDao categoryDao;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        return categoryDao.count(parameters[0], parameters[1]) == 0;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return CategoryModel.NAME + ".not-leaf";
    }
}
