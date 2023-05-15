package org.lpw.clivia.weixin.reply;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(ReplyModel.NAME + ".ctrl")
@Execute(name = "/weixin/reply/", key = ReplyModel.NAME, code = "155")
public class ReplyCtrl {
    @Inject
    private Request request;
    @Inject
    private ReplyService replyService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"})
    })
    public Object query() {
        return replyService.query(request.get("key"), request.get("receiveType"), request.get("receiveMessage"),
                request.getAsInt("state", -1));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 41),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 42),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "receiveType", failureCode = 43),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "receiveType", failureCode = 44),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "receiveMessage", failureCode = 45),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "receiveMessage", failureCode = 46),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "sendType", failureCode = 47),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "sendType", failureCode = 48),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "sendTitle", failureCode = 49),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "sendDescription", failureCode = 50),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "sendPicurl", failureCode = 51),
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"})
    })
    public Object save() {
        replyService.save(request.setToModel(ReplyModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"})
    })
    public Object delete() {
        replyService.delete(request.get("id"));

        return "";
    }
}
