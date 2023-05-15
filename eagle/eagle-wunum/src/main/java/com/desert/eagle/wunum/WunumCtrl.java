package com.desert.eagle.wunum;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(WunumModel.NAME + ".ctrl")
@Execute(name = "", key = WunumModel.NAME, code = "211")
public class WunumCtrl {
    @Inject
    private Message message;
    @Inject
    private Request request;
    @Inject
    private Templates templates;
    @Inject
    private WunumService wunumService;

    @Execute(name = "/wunum/query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return wunumService.query(request.getAsInt("type", -1), request.get("issue"));
    }

    @Execute(name = "/wunum/save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "type", failureCode = 3),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "issue", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "issue", failureCode = 5),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        wunumService.save(request.setToModel(WunumModel.class));

        return "";
    }

    @Execute(name = "/wunum/open", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object open() {
        wunumService.open(request.get("id"));

        return templates.get().success("", message.get(WunumModel.NAME + ".open.done"));
    }

    @Execute(name = "/wunum/delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        wunumService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "/wunum/latest", permit = Permit.always)
    public Object latest() {
        return wunumService.latest(request.get("game"));
    }

    @Execute(name = "/wunum/list", permit = Permit.always)
    public Object list() {
        return wunumService.list(request.getAsInt("type"));
    }

    @Execute(name = "/pks/getLotteryPksInfo.do", permit = Permit.always)
    public Object getLotteryPksInfo() {
        templates.setNopack(true);

        return wunumService.getLotteryPksInfo();
    }
}