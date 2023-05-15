package org.lpw.clivia.module;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ModuleModel.NAME + ".ctrl")
@Execute(name = "/module/", key = ModuleModel.NAME, code = "110")
public class ModuleCtrl {
    @Inject
    private Request request;
    @Inject
    private ModuleService moduleService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return moduleService.query();
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "main", failureCode = 3),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "code", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "code", failureCode = 5),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 6),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "executes", failureCode = 8),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "columns", failureCode = 9),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        moduleService.save(request.setToModel(ModuleModel.class));

        return "";
    }

    @Execute(name = "generate", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = ModuleService.VALIDATOR_EXISTS, parameter = "id", failureCode = 2)
    })
    public Object generate() {
        moduleService.generate(request.get("id"));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        moduleService.delete(request.get("id"));

        return "";
    }
}
