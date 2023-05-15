package org.lpw.clivia.customerservice;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(CustomerserviceModel.NAME + ".ctrl")
@Execute(name = "/customerservice/", key = CustomerserviceModel.NAME, code = "158")
public class CustomerserviceCtrl {
    @Inject
    private Request request;
    @Inject
    private CustomerserviceService customerserviceService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return customerserviceService.query(request.get("type"), request.getAsInt("state", -1));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "type", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "account", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "account", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 5),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        customerserviceService.save(request.setToModel(CustomerserviceModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        customerserviceService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "one", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 1)
    })
    public Object one() {
        return customerserviceService.one(request.get("type"));
    }
}
