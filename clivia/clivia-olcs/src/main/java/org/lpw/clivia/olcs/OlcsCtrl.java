package org.lpw.clivia.olcs;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(OlcsModel.NAME + ".ctrl")
@Execute(name = "/olcs/", key = OlcsModel.NAME, code = "162")
public class OlcsCtrl {
    @Inject
    private Request request;
    @Inject
    private OlcsService olcsService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.ID, parameter = "user", failureCode = 3),
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return olcsService.query(request.get("user"), request.getAsTimestamp("time"));
    }

    @Execute(name = "user", permit = Permit.always)
    public Object user() {
        JSONObject object = olcsService.user(request.getAsTimestamp("time"));
        System.out.println("testtest:user:" + request.get("uid") + ":" + request.get("time") + ":" + object);

        return object;
    }

    @Execute(name = "ask", permit = Permit.always, validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "genre", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "genre", failureCode = 5),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "content", failureCode = 6)
    })
    public Object ask() {
        olcsService.ask(request.get("genre"), request.get("content"));

        return "";
    }

    @Execute(name = "reply", validates = {
            @Validate(validator = Validators.ID, parameter = "user", failureCode = 3),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "genre", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "genre", failureCode = 5),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "content", failureCode = 6),
            @Validate(validator = Validators.SIGN),
    })
    public Object reply() {
        olcsService.reply(request.get("user"), request.get("genre"), request.get("content"));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN),
    })
    public Object delete() {
        olcsService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "clean", validates = {
            @Validate(validator = Validators.ID, parameter = "user", failureCode = 3),
            @Validate(validator = Validators.SIGN),
    })
    public Object clean() {
        olcsService.clean(request.get("user"));

        return "";
    }
}