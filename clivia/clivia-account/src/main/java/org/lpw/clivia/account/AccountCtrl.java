package org.lpw.clivia.account;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(AccountModel.NAME + ".ctrl")
@Execute(name = "/account/", key = AccountModel.NAME, code = "152")
public class AccountCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private AccountService accountService;

    @Execute(name = "query")
    public Object query() {
        return accountService.query(request.get("uid"), request.get("owner"), request.getAsInt("type", -1), request.get("balance"));
    }

    @Execute(name = "query-user", validates = {
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object queryUser() {
        return accountService.queryUser(request.get("user"), request.get("owner"), request.getAsBoolean("fill"));
    }

    @Execute(name = "merge", validates = {
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object merge() {
        return accountService.merge(request.get("user"), request.get("owner"), request.getAsBoolean("fill"));
    }

    @Execute(name = "deposit", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {36}, parameter = "owner", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 9}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "channel", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object deposit() {
        return execute(accountService.deposit(request.get("user"), request.get("owner"), request.getAsInt("type"),
                request.get("channel"), request.getAsLong("amount"), request.getMap()), 11, "deposit");
    }

    @Execute(name = "withdraw", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {36}, parameter = "owner", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 9}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "channel", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object withdraw() {
        return execute(accountService.withdraw(request.get("user"), request.get("owner"), request.getAsInt("type"),
                request.get("channel"), request.getAsLong("amount"), request.getMap()), 12, "withdraw");
    }

    @Execute(name = "reward", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {36}, parameter = "owner", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 9}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "channel", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object reward() {
        return execute(accountService.reward(request.get("user"), request.get("owner"), request.getAsInt("type"),
                request.get("channel"), request.getAsLong("amount")), 13, "reward");
    }

    @Execute(name = "profit", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {36}, parameter = "owner", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 9}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "channel", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object profit() {
        return execute(accountService.profit(request.get("user"), request.get("owner"), request.getAsInt("type"),
                request.get("channel"), request.getAsLong("amount")), 14, "profit");
    }

    @Execute(name = "consume", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {36}, parameter = "owner", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 9}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "channel", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object consume() {
        return execute(accountService.consume(request.get("user"), request.get("owner"), request.getAsInt("type"),
                request.get("channel"), request.getAsLong("amount")), 15, "consume");
    }

    @Execute(name = "remit-in", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {36}, parameter = "owner", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 9}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "channel", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object remitIn() {
        return execute(accountService.remitIn(request.get("user"), request.get("owner"), request.getAsInt("type"),
                request.get("channel"), request.getAsLong("amount")), 16, "remit-in");
    }

    @Execute(name = "remit-out", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {36}, parameter = "owner", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 9}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "channel", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object remitOut() {
        return execute(accountService.remitOut(request.get("user"), request.get("owner"), request.getAsInt("type"),
                request.get("channel"), request.getAsLong("amount")), 17, "remit-out");
    }

    @Execute(name = "refund", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {36}, parameter = "owner", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 9}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "channel", failureCode = 3),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object refund() {
        return execute(accountService.refund(request.get("user"), request.get("owner"), request.getAsInt("type"),
                request.get("channel"), request.getAsLong("amount")), 18, "refund");
    }

    private Object execute(JSONObject object, int code, String type) {
        return object == null ? templates.get().failure(2200 + code, message.get(AccountModel.NAME + "." + type + ".failure"),
                null, null) : object;
    }
}
