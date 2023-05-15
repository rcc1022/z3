package com.desert.eagle.channel;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ChannelModel.NAME + ".ctrl")
@Execute(name = "/channel/", key = ChannelModel.NAME, code = "210")
public class ChannelCtrl {
    @Inject
    private Request request;
    @Inject
    private ChannelService channelService;

    @Execute(name = "query", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object query() {
        return channelService.query();
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "icon", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "qrcode", failureCode = 5),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        channelService.save(request.setToModel(ChannelModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        channelService.delete(request.get("id"));

        return "";
    }
}