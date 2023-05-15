package org.lpw.clivia.shortcut;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ShortcutService.VALIDATOR_CODE_EXISTS)
public class CodeExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private ShortcutService shortcutService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return shortcutService.find(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return ShortcutModel.NAME + ".code.not-exists";
    }
}
