package org.lpw.clivia.transfer;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(TransferService.VALIDATOR_EXISTS)
public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private TransferService transferService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return transferService.find(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return TransferModel.NAME + ".not-exists";
    }
}
