package com.desert.eagle.robot;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(RobotModel.NAME + ".ctrl")
@Execute(name = "/robot/", key = RobotModel.NAME, code = "209")
public class RobotCtrl {
    @Inject
    private Request request;
    @Inject
    private RobotService robotService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return robotService.query();
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "nick", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "avatar", failureCode = 4),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        robotService.save(request.setToModel(RobotModel.class));

        return "";
    }

    @Execute(name = "allot", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object allot() {
        robotService.allot();

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        robotService.delete(request.get("id"));

        return "";
    }
}