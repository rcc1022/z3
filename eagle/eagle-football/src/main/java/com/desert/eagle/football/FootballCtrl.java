package com.desert.eagle.football;

import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(FootballModel.NAME + ".ctrl")
@Execute(name = "/football/", key = FootballModel.NAME, code = "204")
public class FootballCtrl {
    @Inject
    private Request request;
    @Inject
    private FootballService footballService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return footballService.query(request.getAsInt("group", -1), request.get("league"), request.get("teamH"), request.get("teamC"), request.getAsInt("on", -1));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "gid", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "gid", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "running", failureCode = 5),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "league", failureCode = 6),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "gnumH", failureCode = 7),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "gnumC", failureCode = 8),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "teamH", failureCode = 9),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "teanC", failureCode = 10),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "teamIdH", failureCode = 11),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "teamIdC", failureCode = 12),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiu", failureCode = 13),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuH", failureCode = 14),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuC", failureCode = 15),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuShang", failureCode = 16),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuShangH", failureCode = 17),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuShangC", failureCode = 18),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuShangHe", failureCode = 19),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuXia", failureCode = 20),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuXiaH", failureCode = 21),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "rangQiuXiaC", failureCode = 22),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYing", failureCode = 23),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingH", failureCode = 24),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingC", failureCode = 25),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingHe", failureCode = 26),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingShang", failureCode = 27),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingShangH", failureCode = 28),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingShangC", failureCode = 29),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingShangHe", failureCode = 30),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingXia", failureCode = 31),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingXiaH", failureCode = 32),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingXiaC", failureCode = 33),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "duYingXiaHe", failureCode = 34),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        footballService.save(request.setToModel(FootballModel.class));

        return "";
    }

    @Execute(name = "saves", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object saves() {
        footballService.saves(request.getAsInt("group"), request.getAsJsonArray("games"), request.getAsJsonArray("bodans"));

        return "";
    }

    @Execute(name = "on", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object on() {
        footballService.on(request.get("id"), request.getAsInt("on"));

        return "";
    }

    @Execute(name = "all", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object all() {
        footballService.allOnOff(request.getAsInt("on"));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        footballService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "ons", permit = Permit.always)
    public Object ons() {
        return footballService.ons(request.getAsInt("group"));
    }

    @Execute(name = "failure", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object failure() {
        return footballService.failure();
    }
}