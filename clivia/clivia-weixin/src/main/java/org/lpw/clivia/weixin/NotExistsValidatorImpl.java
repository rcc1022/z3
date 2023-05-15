package org.lpw.clivia.weixin;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(WeixinService.VALIDATOR_NOT_EXISTS)
public class NotExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private WeixinService weixinService;

    @Override
    public boolean validate(ValidateWrapper validate, String[] parameters) {
        WeixinModel weixin = weixinService.findByAppId(parameters[1]);

        return weixin == null || weixin.getKey().equals(parameters[0]);
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return WeixinModel.NAME + ".exists";
    }
}
