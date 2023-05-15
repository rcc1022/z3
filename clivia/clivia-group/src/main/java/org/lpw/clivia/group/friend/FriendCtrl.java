package org.lpw.clivia.group.friend;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(FriendModel.NAME + ".ctrl")
@Execute(name = "/group/friend/", key = FriendModel.NAME, code = "159")
public class FriendCtrl {
    @Inject
    private Request request;
    @Inject
    private FriendService friendService;

    @Execute(name = "user", permit = Permit.sign, validates = {@Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object user() {
        return friendService.user();
    }

    @Execute(name = "add", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "user", failureCode = 101),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "memo", failureCode = 102),
            @Validate(validator = UserService.VALIDATOR_SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "user", failureCode = 103)})
    public Object add() {
        friendService.add(request.get("user"), request.get("memo"));

        return "";
    }

    @Execute(name = "agree", permit = Permit.sign, validates = {@Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object agree() {
        friendService.agree(request.get("id"));

        return "";
    }

    @Execute(name = "reject", permit = Permit.sign, validates = {@Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object reject() {
        friendService.reject(request.get("id"));

        return "";
    }
}