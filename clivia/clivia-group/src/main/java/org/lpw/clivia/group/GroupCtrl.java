package org.lpw.clivia.group;

import org.lpw.clivia.Permit;
import org.lpw.clivia.group.member.MemberService;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(GroupModel.NAME + ".ctrl")
@Execute(name = "/group/", key = GroupModel.NAME, code = "159")
public class GroupCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private GroupService groupService;

    @Execute(name = "get", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = UserService.VALIDATOR_SIGN),
            @Validate(validator = GroupService.VALIDATOR_EXISTS, parameter = "id", failureCode = 2),
            @Validate(validator = MemberService.VALIDATOR_IN_GROUP, parameter = "id", failureCode = 104)})
    public Object get() {
        return groupService.get(request.get("id"));
    }

    @Execute(name = "members", validates = {@Validate(validator = Validators.SIGN)})
    public Object members() {
        return groupService.members(request.get("id"));
    }

    @Execute(name = "friends", permit = Permit.sign, validates = {@Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object friends() {
        return groupService.friends();
    }

    @Execute(name = "find", permit = Permit.sign, validates = {@Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object find() {
        return groupService.find(request.get("idUidCode"));
    }

    @Execute(name = "start", permit = Permit.sign, validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "avatar", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object start() {
        int n = groupService.start(request.get("name"), request.get("avatar"), request.get("prologue"), request.getAsArray("users"));

        return n == 0 ? "" : templates.get().failure(159004 + n, message.get(GroupModel.NAME + ".start." + n), null, null);
    }

    @Execute(name = "member", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object member() {
        int n = groupService.member(request.get("id"), request.getAsArray("users"));

        return n == 0 ? "" : templates.get().failure(159007 + n, message.get(GroupModel.NAME + ".manage." + n), null, null);
    }

    @Execute(name = "avatar", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "avatar", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object avatar() {
        int n = groupService.avatar(request.get("id"), request.get("avatar"));

        return n == 0 ? "" : templates.get().failure(159007 + n, message.get(GroupModel.NAME + ".manage." + n), null, null);
    }

    @Execute(name = "name", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 3),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object name() {
        int n = groupService.groupName(request.get("id"), request.get("name"));

        return n == 0 ? "" : templates.get().failure(159007 + n, message.get(GroupModel.NAME + ".manage." + n), null, null);
    }

    @Execute(name = "notice", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object notice() {
        int n = groupService.notice(request.get("id"), request.get("notice"));

        return n == 0 ? "" : templates.get().failure(159007 + n, message.get(GroupModel.NAME + ".manage." + n), null, null);
    }

    @Execute(name = "delete", permit = Permit.sign, validates = {@Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object delete() {
        groupService.delete(request.get("id"));

        return "";
    }
}