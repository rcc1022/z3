package org.lpw.clivia.upgrader;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UpgraderModel.NAME + ".ctrl")
@Execute(name = "/upgrader/", key = UpgraderModel.NAME, code = "107")
public class UpgraderCtrl {
    @Inject
    private Request request;
    @Inject
    private UpgraderService upgraderService;

    @Execute(name = "query", validates = {@Validate(validator = Validators.SIGN)})
    public Object query() {
        return upgraderService.query();
    }

    @Execute(name = "latest", permit = Permit.always)
    public Object latest() {
        return upgraderService.latest(request.get("client"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.GREATER_THAN, number = {0}, parameter = "version", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 4),
            @Validate(validator = Validators.BETWEEN, number = {0, 1}, parameter = "forced", failureCode = 5),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "explain", failureCode = 6),
            @Validate(validator = Validators.SIGN)})
    public Object save() {
        upgraderService.save(request.setToModel(UpgraderModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {@Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)})
    public Object delete() {
        upgraderService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "plist", permit = Permit.always, type = Templates.FREEMARKER)
    public Object plist() {
        return upgraderService.plist();
    }
}
