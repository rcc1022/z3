package org.lpw.clivia.user;

import org.lpw.clivia.Permit;
import org.lpw.clivia.push.PushService;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.clivia.user.invitecode.InvitecodeService;
import org.lpw.clivia.user.inviter.InviterService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UserModel.NAME + ".ctrl")
@Execute(name = "/user/", key = UserModel.NAME, code = "151")
public class UserCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private PushService pushService;
    @Inject
    private UserService userService;
    @Inject
    private InviterService inviterService;

    @Execute(name = "inviter", permit = Permit.always)
    public Object inviter() {
        inviterService.set(request.get("code"));

        return "";
    }

    @Execute(name = "sign-up-sms", permit = Permit.always, validates = {
            @Validate(validator = Validators.MOBILE, parameter = "mobile", failureCode = 10)})
    public Object signUpSms() {
        return pushService.captcha(UserService.SMS_SIGN_UP, request.get("mobile"));
    }

    @Execute(name = "sign-up", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uid", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "uid", failureCode = 2),
            @Validate(validator = UserService.VALIDATOR_EXISTS_TYPE, parameter = "type", failureCode = 27),
            @Validate(validator = UserService.VALIDATOR_PASSWORD, parameters = {"password", "type"}, failureCode = 3),
            @Validate(validator = UserService.VALIDATOR_SIGN_UP_ENABLE, failureCode = 99),
            @Validate(validator = InvitecodeService.VALIDATOR_VALID, parameter = "invitecode", failureCode = 29),
            @Validate(validator = UserService.VALIDATOR_SIGN_UP_SMS, parameter = "sms", failureCode = 98),
            @Validate(validator = AuthService.VALIDATOR_UID_NOT_EXISTS, parameters = {"uid", "password", "type"}, failureCode = 4)})
    public Object signUp() {
        userService.signUp(request.get("uid"), request.get("password"), request.get("type"),
                request.get("inviter"), request.get("grade"), request.get("invitecode"));

        return sign();
    }

    @Execute(name = "sign-in-sms", permit = Permit.always, validates = {
            @Validate(validator = Validators.MOBILE, parameter = "mobile", failureCode = 10)})
    public Object signInSms() {
        return pushService.captcha(UserService.SMS_SIGN_IN, request.get("mobile"));
    }

    @Execute(name = "sign-in", permit = Permit.always, validates = {
            @Validate(validator = UserService.VALIDATOR_EXISTS_TYPE, parameter = "type", failureCode = 27),
            @Validate(validator = UserService.VALIDATOR_PASSWORD, parameters = {"password", "type"}, failureCode = 3),
            @Validate(validator = UserService.VALIDATOR_SIGN_IN, parameters = {"uid", "password", "type", "grade"}, failureCode = 6)})
    public Object signIn() {
        return sign();
    }

    @Execute(name = "sign-in-password", permit = Permit.always)
    public Object signInPassword() {
        return userService.signInPassword(request.get("id"), request.get("password")) ? ""
                : templates.get().failure(151006, message.get(UserModel.NAME + ".password.failure"),
                null, null);
    }

    @Execute(name = "sign-in-gesture", permit = Permit.always)
    public Object signInGesture() {
        return userService.signInGesture(request.get("id"), request.get("gesture")) ? ""
                : templates.get().failure(151018, message.get(UserModel.NAME + ".gesture.failure"),
                null, null);
    }

    @Execute(name = "sign", permit = Permit.always)
    public Object sign() {
        return userService.sign();
    }

    @Execute(name = "sign-out", permit = Permit.always)
    public Object signOut() {
        userService.signOut();

        return "";
    }

    @Execute(name = "modify", permit = Permit.sign, validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "idcard", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 9),
            @Validate(validator = Validators.MOBILE, emptyable = true, parameter = "mobile", failureCode = 10),
            @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "email", failureCode = 12),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "weixin", failureCode = 33),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "qq", failureCode = 34),
            @Validate(validator = Validators.BETWEEN, number = {0, 2}, parameter = "gender", failureCode = 13),
            @Validate(validator = Validators.MAX_LENGTH, number = {200}, parameter = "avatar", failureCode = 32),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "signature", failureCode = 36),
            @Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object modify() {
        userService.modify(request.setToModel(UserModel.class));

        return sign();
    }

    @Execute(name = "password", permit = Permit.sign, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "new", failureCode = 14, failureArgKeys = {UserModel.NAME + ".password.new"}),
            @Validate(validator = Validators.NOT_EQUALS, parameters = {"old", "new"}, failureCode = 15, failureArgKeys = {UserModel.NAME + ".password.new", UserModel.NAME + ".password.old"}),
            @Validate(validator = Validators.EQUALS, parameters = {"new", "repeat"}, failureCode = 16, failureArgKeys = {UserModel.NAME + ".password.repeat", UserModel.NAME + ".password.new"}),
            @Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object password() {
        return userService.password(request.get("old"), request.get("new")) ? ""
                : templates.get().failure(151017, message.get(UserModel.NAME + ".password.illegal"),
                null, null);
    }

    @Execute(name = "reset-password-sms", permit = Permit.always, validates = {
            @Validate(validator = Validators.MOBILE, parameter = "mobile", failureCode = 10)})
    public Object resetPasswordSms() {
        return pushService.captcha(UserService.SMS_RESET_PASSWORD, request.get("mobile"));
    }

    @Execute(name = "sms-reset-password", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "password", failureCode = 14, failureArgKeys = {UserModel.NAME + ".password.new"}),
            @Validate(validator = Validators.MOBILE, parameter = "mobile", failureCode = 10),
            @Validate(validator = PushService.VALIDATOR_CAPTCHA, parameter = "sms", failureCode = 35),
            @Validate(validator = AuthService.VALIDATOR_UID_EXISTS, parameter = "mobile", failureCode = 28, failureKey = UserModel.NAME
                    + ".mobile.not-exists")})
    public Object smsResetPassword() {
        userService.resetPassword(request.get("mobile"), request.get("password"));

        return "";
    }

    @Execute(name = "get", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "ids", failureCode = 21),
            @Validate(validator = Validators.SIGN)})
    public Object get() {
        return userService.get(request.getAsArray("ids"));
    }

    @Execute(name = "find-by-code", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "code", failureCode = 26),
            @Validate(validator = Validators.SIGN)})
    public Object findByCode() {
        return userService.findByCode(request.get("code"));
    }

    @Execute(name = "find-by-uid", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uid", failureCode = 27),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = AuthService.VALIDATOR_UID_EXISTS, parameter = "uid", failureCode = 28)})
    public Object findByUid() {
        return userService.findByUid(request.get("uid"));
    }

    @Execute(name = "find-sign", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "idUidCode", failureCode = 31),
            @Validate(validator = Validators.SIGN)})
    public Object findOrSign() {
        return userService.findOrSign(request.get("idUidCode"));
    }

    @Execute(name = "find", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "idUidCode", failureCode = 31)})
    public Object find() {
        return userService.find(request.get("idUidCode"));
    }

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.MOBILE, emptyable = true, parameter = "mobile", failureCode = 10),
            @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 11),
            @Validate(validator = Validators.SIGN)})
    public Object query() {
        return userService.query(request.get("uid"), request.get("idcard"), request.get("name"),
                request.get("nick"), request.get("mobile"), request.get("email"), request.get("weixin"),
                request.get("qq"), request.get("code"), request.getAsInt("minGrade", -1),
                request.getAsInt("maxGrade", -1), request.getAsInt("state", -1),
                request.get("register"), request.get("from"));
    }

    @Execute(name = "new", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uid", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "uid", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "idcard", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 9),
            @Validate(validator = Validators.MOBILE, emptyable = true, parameter = "mobile", failureCode = 10),
            @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "email", failureCode = 12),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "weixin", failureCode = 33),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "qq", failureCode = 34),
            @Validate(validator = Validators.BETWEEN, number = {0, 2}, parameter = "gender", failureCode = 13),
            @Validate(validator = Validators.MAX_LENGTH, number = {200}, parameter = "avatar", failureCode = 32),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "signature", failureCode = 36),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 24),
            @Validate(validator = AuthService.VALIDATOR_UID_NOT_EXISTS, parameter = "uid", failureCode = 4)})
    public Object create() {
        userService.create(request.get("uid"), request.get("password"), request.get("idcard"),
                request.get("name"), request.get("nick"), request.get("mobile"), request.get("email"),
                request.get("weixin"), request.get("qq"), request.get("avatar"),
                request.getAsInt("gender"), request.getAsSqlDate("birthday"), userService.id(),
                request.getAsInt("grade"), request.getAsInt("state"));

        return "";
    }

    @Execute(name = "update", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "idcard", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 9),
            @Validate(validator = Validators.MOBILE, emptyable = true, parameter = "mobile", failureCode = 10),
            @Validate(validator = Validators.EMAIL, emptyable = true, parameter = "email", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "email", failureCode = 12),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "weixin", failureCode = 33),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "qq", failureCode = 34),
            @Validate(validator = Validators.BETWEEN, number = {0, 2}, parameter = "gender", failureCode = 13),
            @Validate(validator = Validators.MAX_LENGTH, number = {200}, parameter = "avatar", failureCode = 32),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "signature", failureCode = 36),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 24),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)})
    public Object update() {
        userService.update(request.setToModel(UserModel.class));

        return "";
    }

    @Execute(name = "reset-password", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)})
    public Object resetPassword() {
        String password = userService.resetPassword(request.get("id"));

        return templates.get().success(password, UserModel.NAME + ".reset-password.success", password);
    }

    @Execute(name = "grade", validates = {@Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.BETWEEN, number = {0, 98}, parameter = "grade", failureCode = 23),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)})
    public Object grade() {
        userService.grade(request.get("id"), request.getAsInt("grade"));

        return "";
    }

    @Execute(name = "state", validates = {@Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 24),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)})
    public Object state() {
        userService.state(request.get("id"), request.getAsInt("state"));

        return "";
    }

    @Execute(name = "count", validates = {@Validate(validator = Validators.SIGN)})
    public Object count() {
        return userService.count();
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 22),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = UserService.VALIDATOR_EXISTS, parameter = "id", failureCode = 25)})
    public Object delete() {
        userService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "destroy", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)})
    public Object destroy() {
        return userService.destroy(request.get("password"));
    }
}
