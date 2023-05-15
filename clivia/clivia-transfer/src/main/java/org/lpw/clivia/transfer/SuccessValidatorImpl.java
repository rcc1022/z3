package org.lpw.clivia.transfer;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(TransferService.VALIDATOR_SUCCESS)
public class SuccessValidatorImpl extends ValidatorSupport {
    @Inject
    private TransferService transferService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return transferService.find(parameter).getState() == 0;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return TransferModel.NAME + ".success.disable";
    }
}
