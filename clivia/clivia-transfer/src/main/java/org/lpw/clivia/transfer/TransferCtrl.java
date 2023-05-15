package org.lpw.clivia.transfer;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(TransferModel.NAME + ".ctrl")
@Execute(name = "/transfer/", key = TransferModel.NAME, code = "154")
public class TransferCtrl {
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private Request request;
    @Inject
    private TransferService transferService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        String state = request.get("state");
        if (validator.isEmpty(state))
            state = "-1";

        return transferService.query(request.get("type"), request.get("appId"), request.get("user"), request.get("orderNo"),
                request.get("billNo"), request.get("tradeNo"), numeric.toInt(state), request.get("start"));
    }

    @Execute(name = "success", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = TransferService.VALIDATOR_EXISTS, parameter = "id", failureCode = 2),
            @Validate(validator = TransferService.VALIDATOR_SUCCESS, parameter = "id", failureCode = 3)
    })
    public Object success() {
        return transferService.success(request.get("id"), request.getMap());
    }

    @Execute(name = "notice", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = TransferService.VALIDATOR_EXISTS, parameter = "id", failureCode = 2)
    })
    public Object notice() {
        return transferService.notice(request.get("id"));
    }

    @Execute(name = "create", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "type", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "appId", failureCode = 5),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "account", failureCode = 6),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "account", failureCode = 7),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "billNo", failureCode = 9),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 10)
    })
    public Object create() {
        return transferService.create(request.get("type"), request.get("appId"), request.get("user"), request.get("account"),
                request.getAsInt("amount"), request.get("billNo"), request.get("notice"), request.getMap());
    }

    @Execute(name = "complete", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "orderNo", failureCode = 11),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 7),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "tradeNo", failureCode = 12),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "tradeNo", failureCode = 13),
            @Validate(validator = Validators.BETWEEN, number = {1, 2}, parameter = "state", failureCode = 14),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = TransferService.VALIDATOR_EXISTS, parameter = "orderNo", failureCode = 15)
    })
    public Object complete() {
        return transferService.complete(request.get("orderNo"), request.getAsInt("amount"), request.get("tradeNo"),
                request.getAsInt("state"), request.getMap());
    }
}
