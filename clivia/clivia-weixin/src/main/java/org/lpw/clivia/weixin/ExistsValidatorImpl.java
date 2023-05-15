package org.lpw.clivia.weixin;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(WeixinService.VALIDATOR_EXISTS)
public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private WeixinService weixinService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return weixinService.findByKey(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return WeixinModel.NAME + ".not-exists";
    }
}
