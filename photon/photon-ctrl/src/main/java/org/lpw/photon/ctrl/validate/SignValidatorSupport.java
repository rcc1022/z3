package org.lpw.photon.ctrl.validate;

import org.lpw.photon.crypto.Sign;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.security.TrustfulIp;
import org.lpw.photon.util.Logger;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;

public class SignValidatorSupport extends ValidatorSupport implements SignValidator {
    @Inject
    private Sign sign;
    @Inject
    private Logger logger;
    @Inject
    private Header header;
    @Inject
    private TrustfulIp trustfulIp;
    @Value("${photon.ctrl.illegal-sign:999995}")
    private int failureCode;
    private final ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    @Override
    public void setSignEnable(boolean enable) {
        threadLocal.set(enable);
    }

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        if (!enable() || trustfulIp.contains(header.getIp())
                || sign.verify(request.getMap(), validator.isEmpty(validate.getString()) ? null : validate.getString()[0]))
            return true;

        logger.warn(null, "参数[{}]签名验证不通过！", converter.toString(request.getMap()));

        return false;
    }

    private boolean enable() {
        return threadLocal.get() == null || threadLocal.get();
    }

    @Override
    public int getFailureCode(ValidateWrapper validate) {
        return failureCode;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-sign";
    }
}
