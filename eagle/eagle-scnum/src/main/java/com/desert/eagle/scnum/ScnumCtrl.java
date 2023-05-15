package com.desert.eagle.scnum;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ScnumModel.NAME + ".ctrl")
@Execute(name = "", key = ScnumModel.NAME, code = "207")
public class ScnumCtrl {
    @Inject private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private ScnumService scnumService;

    @Execute(name = "/scnum/query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return scnumService.query(request.getAsInt("type", -1), request.get("issue"));
    }

    @Execute(name = "/scnum/save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 3),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "issue", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "issue", failureCode = 5),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        scnumService.save(request.setToModel(ScnumModel.class));

        return "";
    }

    @Execute(name = "/scnum/open", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object open() {
        scnumService.open(request.get("id"));

        return templates.get().success("", message.get(ScnumModel.NAME + ".open.done"));
    }

    @Execute(name = "/scnum/delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        scnumService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "/scnum/latest", permit = Permit.always)
    public Object latest() {
        return scnumService.latest(request.get("game"));
    }

    @Execute(name = "/scnum/list", permit = Permit.always)
    public Object list() {
        return scnumService.list(request.getAsInt("type"));
    }

    @Execute(name = "/pks/getLotteryPksInfo.do", permit = Permit.always)
    public Object getLotteryPksInfo() {
        templates.setNopack(true);

        return scnumService.getLotteryPksInfo();
    }
}