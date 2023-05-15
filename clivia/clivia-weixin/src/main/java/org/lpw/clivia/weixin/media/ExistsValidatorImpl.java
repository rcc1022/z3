package org.lpw.clivia.weixin.media;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(MediaService.VALIDATOR_EXISTS)
public class ExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private MediaDao mediaDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return mediaDao.findById(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return MediaModel.NAME + ".not-exists";
    }
}
