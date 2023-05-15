package org.lpw.clivia.device;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(DeviceModel.NAME + ".ctrl")
@Execute(name = "/device/", key = DeviceModel.NAME, code = "161")
public class DeviceCtrl {
    @Inject
    private Request request;
    @Inject
    private DeviceService deviceService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return deviceService.query();
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "type", failureCode = 4),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "identifier", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "identifier", failureCode = 6),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "lang", failureCode = 7),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        deviceService.save(request.get("type"), request.get("identifier"), request.get("lang"));

        return "";
    }
}