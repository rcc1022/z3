package com.desert.eagle.message;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(MessageModel.NAME + ".ctrl")
@Execute(name = "/message/", key = MessageModel.NAME, code = "206")
public class MessageCtrl {
    @Inject
    private Request request;
    @Inject
    private MessageService messageService;

    @Execute(name = "query", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object query() {
        return messageService.query(request.get("game"), request.getAsLong("time"));
    }
}