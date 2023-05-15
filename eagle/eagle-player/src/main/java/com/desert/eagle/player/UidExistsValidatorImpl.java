package com.desert.eagle.player;

import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(PlayerService.VALIDATOR_UID_EXISTS)
public class UidExistsValidatorImpl extends ValidatorSupport {
    @Inject
    private PlayerDao playerDao;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        return playerDao.find(parameter) != null;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return PlayerModel.NAME + ".uid.not-exists";
    }
}
