package org.lpw.photon.ctrl.validate;

import org.springframework.stereotype.Controller;

@Controller(Validators.ID)
public class IdValidatorImpl extends IdValidatorSupport {
    @Override
    protected String getDefaultFailureMessageKey() {
        return Validators.PREFIX + "illegal-id";
    }
}
