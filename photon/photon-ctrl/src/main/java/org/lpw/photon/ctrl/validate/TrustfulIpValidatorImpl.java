package org.lpw.photon.ctrl.validate;

import org.lpw.photon.ctrl.security.TrustfulIp;
import org.lpw.photon.ctrl.context.Header;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(Validators.TRUSTFUL_IP)
public class TrustfulIpValidatorImpl extends ValidatorSupport {
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return trustfulIp.contains(header.getIp());
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return 9996;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "distrust-ip";
    }
}
