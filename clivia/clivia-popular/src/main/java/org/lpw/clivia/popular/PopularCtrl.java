package org.lpw.clivia.popular;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(PopularModel.NAME + ".ctrl")
@Execute(name = "/popular/", key = PopularModel.NAME, code = "103")
public class PopularCtrl {
    @Inject
    private Request request;
    @Inject
    private PopularService popularService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return popularService.query(request.get("key"), request.getAsInt("state", -1));
    }

    @Execute(name = "publish", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2)
    })
    public Object publish() {
        return popularService.publish(request.get("key"), request.getAsInt("size", 10));
    }

    @Execute(name = "state", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "state", failureCode = 3),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = PopularService.VALIDATOR_EXISTS, parameter = "id", failureCode = 4)
    })
    public Object state() {
        popularService.state(request.get("id"), request.getAsInt("state"));

        return "";
    }

    @Execute(name = "increase", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 6),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "value", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "value", failureCode = 8),
            @Validate(validator = Validators.SIGN)
    })
    public Object increase() {
        popularService.increase(request.get("key"), request.get("value"));

        return "";
    }
}
