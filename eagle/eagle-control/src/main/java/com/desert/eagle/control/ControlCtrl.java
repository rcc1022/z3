package com.desert.eagle.control;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ControlModel.NAME + ".ctrl")
@Execute(name = "/control/", key = ControlModel.NAME, code = "209")
public class ControlCtrl {
    @Inject
    private Request request;
    @Inject
    private ControlService controlService;

    @Execute(name = "query,single-query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return controlService.query(request.getAsInt("mode"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        controlService.save(request.setToModel(ControlModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        controlService.delete(request.get("id"));

        return "";
    }
}