package com.desert.eagle.game;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(GameModel.NAME + ".ctrl")
@Execute(name = "/game/", key = GameModel.NAME, code = "202")
public class GameCtrl {
    @Inject
    private Request request;
    @Inject
    private GameService gameService;

    @Execute(name = "query", permit = Permit.always)
    public Object query() {
        return gameService.query(request.getAsInt("on", -1));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 4),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "cover", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "cover", failureCode = 6),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        gameService.save(request.setToModel(GameModel.class));

        return "";
    }

    @Execute(name = "on", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object on() {
        gameService.on(request.get("id"), request.getAsInt("on"));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        gameService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "rule", permit = Permit.always, validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1)
    })
    public Object rule() {
        return gameService.rule(request.get("id"));
    }
}