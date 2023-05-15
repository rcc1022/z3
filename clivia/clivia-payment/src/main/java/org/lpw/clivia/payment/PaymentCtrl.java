package org.lpw.clivia.payment;

import javax.inject.Inject;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

@Controller(PaymentModel.NAME + ".ctrl")
@Execute(name = "/payment/", key = PaymentModel.NAME, code = "153")
public class PaymentCtrl {
    @Inject
    private Request request;
    @Inject
    private UserService userService;
    @Inject
    private PaymentService paymentService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return query(request.get("user"));
    }

    @Execute(name = "uquery", validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object uquery() {
        return query(userService.id());
    }

    private Object query(String user) {
        return paymentService.query(request.get("type"), request.get("appId"), user, request.get("orderNo"),
                request.get("billNo"), request.get("tradeNo"), request.getAsInt("state", -1), request.get("start"));
    }

    @Execute(name = "success", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = PaymentService.VALIDATOR_EXISTS, parameter = "id", failureCode = 11),
            @Validate(validator = PaymentService.VALIDATOR_SUCCESS, parameter = "id", failureCode = 12)
    })
    public Object success() {
        return paymentService.success(request.get("id"), request.getMap());
    }

    @Execute(name = "notice", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = PaymentService.VALIDATOR_EXISTS, parameter = "id", failureCode = 11)
    })
    public Object notice() {
        return paymentService.notice(request.get("id"));
    }

    @Execute(name = "create", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "type", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "appId", failureCode = 13),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "billNo", failureCode = 14),
            @Validate(validator = UserService.VALIDATOR_EXISTS_SIGN, parameter = "user", failureCode = 5)
    })
    public Object create() {
        return paymentService.create(request.get("type"), request.get("appId"), request.get("user"), request.getAsInt("amount"),
                request.get("billNo"), request.get("notice"), request.getMap());
    }

    @Execute(name = "complete", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "orderNo", failureCode = 6),
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "amount", failureCode = 4),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "tradeNo", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "tradeNo", failureCode = 8),
            @Validate(validator = Validators.BETWEEN, number = {1, 2}, parameter = "state", failureCode = 9),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = PaymentService.VALIDATOR_EXISTS, parameter = "orderNo", failureCode = 10)
    })
    public Object complete() {
        return paymentService.complete(request.get("orderNo"), request.getAsInt("amount"), request.get("tradeNo"),
                request.getAsInt("state"), request.getMap());
    }
}
