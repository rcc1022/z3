package org.lpw.clivia.user.upload;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UploadModel.NAME + ".ctrl")
@Execute(name = "/user/upload/", key = UploadModel.NAME, code = "151")
public class UploadCtrl {
    @Inject
    private Request request;
    @Inject
    private UploadService uploadService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return uploadService.query();
    }

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return uploadService.user();
    }

    @Execute(name = "save", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 111),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "filename", failureCode = 113),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "filename", failureCode = 114),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object save() {
        uploadService.save(request.get("id"), request.get("filename"));

        return "";
    }

    @Execute(name = "delete", permit = Permit.sign, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 111),
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object delete() {
        uploadService.delete(request.get("id"));

        return "";
    }
}