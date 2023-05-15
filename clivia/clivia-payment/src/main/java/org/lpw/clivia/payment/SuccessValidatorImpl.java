package org.lpw.clivia.payment;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(PaymentService.VALIDATOR_SUCCESS)
public class SuccessValidatorImpl extends ValidatorSupport {
    @Inject
    private PaymentService paymentService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return paymentService.find(parameter).getState() == 0;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return PaymentModel.NAME + ".success.disable";
    }
}
