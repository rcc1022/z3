package org.lpw.clivia.user.crosier;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(CrosierModel.NAME + ".ctrl")
@Execute(name = "/user/crosier/", key = CrosierModel.NAME, code = "151")
public class CrosierCtrl {
    @Inject
    private Request request;
    @Inject
    private CrosierService crosierService;

    @Execute(name = "sign-up-grades", permit = Permit.always)
    public Object signUpGrades() {
        return crosierService.signUpGrades();
    }

    @Execute(name = "grades", permit = Permit.sign, validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object grades() {
        return crosierService.grades();
    }

    @Execute(name = "pathes", permit = Permit.sign, validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object pathes() {
        return crosierService.pathes(request.getAsInt("grade"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        crosierService.save(request.getAsInt("grade"), request.get("pathes"));

        return "";
    }
}
