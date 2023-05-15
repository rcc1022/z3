package org.lpw.clivia.group.member;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(MemberModel.NAME + ".ctrl")
@Execute(name = "/group/member/", key = MemberModel.NAME, code = "159")
public class MemberCtrl {
    @Inject
    private Request request;
    @Inject
    private MemberService memberService;

    @Execute(name = "groups", validates = {@Validate(validator = Validators.SIGN)})
    public Object groups() {
        return memberService.groups(request.get("user"));
    }

    @Execute(name = "memo", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 101),
            @Validate(validator = Validators.ID, parameter = "group", failureCode = 102),
            @Validate(validator = UserService.VALIDATOR_SIGN),
            @Validate(validator = MemberService.VALIDATOR_EXISTS, parameter = "id", failureCode = 103),
            @Validate(validator = MemberService.VALIDATOR_IN_GROUP, parameter = "group", failureCode = 104)})
    public Object memo() {
        memberService.memo(request.get("id"), request.get("memo"));

        return "";
    }
}