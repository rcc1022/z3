package com.desert.eagle.player.withdraw;

import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.photon.ctrl.validate.ValidateWrapper;
import org.lpw.photon.ctrl.validate.ValidatorSupport;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(WithdrawService.TIME_RANGE_VALIDATOR)
public class TimeRangeValidatorImpl extends ValidatorSupport {
    @Inject
    private DateTime dateTime;
    @Inject
    private KeyvalueService keyvalueService;

    @Override
    public boolean validate(ValidateWrapper validate, String parameter) {
        String range = keyvalueService.value("setting.withdraw.time");
        if (validator.isEmpty(range) || range.indexOf(',') == -1)
            return true;

        String[] array = range.split(",");
        String time = dateTime.toString(dateTime.now(), "HH:mm:ss");

        return time.compareTo(array[0]) >= 0 && time.compareTo(array[1]) <= 0;
    }

    @Override
    protected String getDefaultFailureMessageKey() {
        return WithdrawModel.NAME + ".time-range.illegal";
    }
}
