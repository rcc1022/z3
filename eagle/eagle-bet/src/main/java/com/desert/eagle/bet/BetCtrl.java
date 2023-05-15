package com.desert.eagle.bet;

import com.desert.eagle.util.ParameterHelper;
import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(BetModel.NAME + ".ctrl")
@Execute(name = "/bet/,/sayhia/", key = BetModel.NAME, code = "205")
public class BetCtrl {
    @Inject
    private Request request;
    @Inject
    private ParameterHelper parameterHelper;
    @Inject
    private BetService betService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return betService.query(request.get("id"), request.get("game"), request.get("uid"), request.get("issue"), request.getAsInt("win", -1), request.get("time"));
    }

    /**
     * 查询即时下单记录
     * @return
     */
    @Execute(name = "immediately", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object immediately() {
        return betService.immediately();
    }

    @Execute(name = "game", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object game() {
        String game = parameterHelper.get(BetModel.NAME + "game", "game");
        if ("all".equals(game))
            game = "";
        String date = parameterHelper.get(BetModel.NAME + "game", "date");
        date = date + "," + date;

        return betService.query(null, game, request.get("uid"), request.get("issue"), request.getAsInt("win", -1), date);
    }

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return betService.user(request.get("game"), request.get("time"));
    }

    @Execute(name = "save", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object save() {
        return betService.save(request.get("game"), request.get("issue"), request.getAsJsonArray("items"));
    }

    @Execute(name = "cancel", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object cancel() {
        return betService.cancel(request.get("game"));
    }

    @Execute(name = "zhuihao", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object zhuihao() {
        return betService.zhuihao(request.get("game"), request.getAsLong("issue"), request.get("type"), request.get("name"),
                request.getAsInt("amount"), request.getAsInt("count"), request.getAsInt("multiple"), request.getAsInt("stop"));
    }

    @Execute(name = "sum1", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object sum1() {
        return betService.sum1(request.get("uid"), request.get("nick"), request.get("game"), request.get("times"));
    }

    @Execute(name = "sum2", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object sum2() {
        return betService.sum2(request.get("uid"), request.get("nick"), request.get("invitor"),
                request.get("game"), request.get("times"), request.getAsInt("timeType", -1));
    }

    @Execute(name = "total", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object total() {
        return betService.total(request.get("game"));
    }

    @Execute(name = "unsettle", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object unsettle() {
        return "";
    }

    @Execute(name = "clear", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object clear() {
        betService.clear(request.get("game"), request.get("issue"));

        return "";
    }
}