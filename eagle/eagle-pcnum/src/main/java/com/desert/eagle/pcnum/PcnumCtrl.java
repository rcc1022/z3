package com.desert.eagle.pcnum;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(PcnumModel.NAME + ".ctrl")
@Execute(name = "/pcnum/", key = PcnumModel.NAME, code = "203")
public class PcnumCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private PcnumService pcnumService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return pcnumService.query(request.getAsInt("type", -1), request.get("issue"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 3),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "issue", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "issue", failureCode = 5),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        pcnumService.save(request.setToModel(PcnumModel.class));

        return "";
    }

    @Execute(name = "open", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object open() {
        pcnumService.open(request.get("id"));

        return templates.get().success("", message.get(PcnumModel.NAME + ".open.done"));
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        pcnumService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "latest", permit = Permit.always)
    public Object latest() {
        return pcnumService.latest(request.get("game"));
    }

    @Execute(name = "list", permit = Permit.always)
    public Object list() {
        return pcnumService.list(request.getAsInt("type"));
    }
}