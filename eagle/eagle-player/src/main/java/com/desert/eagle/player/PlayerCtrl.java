package com.desert.eagle.player;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(PlayerModel.NAME + ".ctrl")
@Execute(name = "/player/", key = PlayerModel.NAME, code = "201")
public class PlayerCtrl {
    @Inject
    private Message message;
    @Inject
    private Header header;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private PlayerService playerService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return playerService.query(false, request.get("uid"), request.get("nick"), request.get("memo"),
                request.getAsInt("ban", -1), request.get("time"), null);
    }

    @Execute(name = "subquery", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object subquery() {
        return playerService.subquery(request.get("id"));
    }

    @Execute(name = "juniors", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object juniors() {
        return playerService.query(true, request.get("uid"), request.get("nick"), request.get("memo"),
                request.getAsInt("ban", -1), null, request.getAsSqlDate("date"));
    }

    @Execute(name = "inviter", permit = Permit.always)
    public Object inviter() {
        playerService.inviter(request.get("code"));

        return "";
    }

    @Execute(name = "sign", permit = Permit.always)
    public Object sign() {
        return playerService.sign();
    }

    @Execute(name = "qr", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object qr() {
        return playerService.qr(request.get("host"));
    }

    @Execute(name = "save", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object save() {
        playerService.save(request.get("mobile"));

        return "";
    }

    @Execute(name = "memo", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object memo() {
        playerService.memo(request.get("id"), request.get("memo"));

        return "";
    }

    @Execute(name = "deposit", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object deposit() {
        boolean success = playerService.deposit(request.get("id"), request.getAsInt("amount"));

        return success ? "" : templates.get().failure(201051, message.get(PlayerModel.NAME + ".balance.not-enough"), null, null);
    }

    @Execute(name = "gift", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object gift() {
        playerService.gift(request.get("id"), request.getAsInt("giftAmount"));

        return "";
    }

    @Execute(name = "junior", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object junior() {
        return playerService.junior();
    }

    @Execute(name = "ip", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object ip() {
        playerService.ip(header.getIp());

        return "";
    }

    @Execute(name = "ban", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object ban() {
        playerService.ban(request.get("id"), request.getAsInt("ban"));

        return "";
    }

    @Execute(name = "transfer", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object transfer() {
        playerService.transfer();

        return "";
    }

    @Execute(name = "invite", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object invite() {
        playerService.invite(request.get("id"), request.get("newInvitor"));

        return "";
    }
}