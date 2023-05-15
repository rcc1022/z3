package org.lpw.clivia.dict;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(DictModel.NAME + ".ctrl")
@Execute(name = "/dict/", key = DictModel.NAME, code = "111")
public class DictCtrl {
    @Inject
    private Request request;
    @Inject
    private DictService dictService;

    @Execute(name = "query", validates = { @Validate(validator = Validators.SIGN) })
    public Object query() {
        return dictService.query(request.get("key"));
    }

    @Execute(name = "list", permit = Permit.always)
    public Object list() {
        return dictService.list(request.get("key"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = { 100 }, parameter = "key", failureCode = 4),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "value", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = { 100 }, parameter = "value", failureCode = 6),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = { 100 }, parameter = "name", failureCode = 8),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = DictService.VALIDATOR_VALUE_NOT_EXISTS, parameters = { "id", "key",
                    "value" }, failureCode = 9) })
    public Object save() {
        dictService.save(request.setToModel(DictModel.class));

        return "";
    }

    @Execute(name = "delete", validates = { @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN) })
    public Object delete() {
        dictService.delete(request.get("id"));

        return "";
    }
}