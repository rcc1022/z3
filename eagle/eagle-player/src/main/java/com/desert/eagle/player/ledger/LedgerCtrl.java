package com.desert.eagle.player.ledger;

import com.desert.eagle.util.ParameterHelper;
import org.lpw.clivia.Permit;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(LedgerModel.NAME + ".ctrl")
@Execute(name = "/player/ledger/", key = LedgerModel.NAME, code = "201")
public class LedgerCtrl {
    @Inject
    private Request request;
    @Inject
    private ParameterHelper parameterHelper;
    @Inject
    private LedgerService ledgerService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return ledgerService.query(request.get("uid"), request.get("nick"), request.get("invitor"), request.get("type"), request.get("time"));
    }

    @Execute(name = "player", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object player() {
        return ledgerService.query(parameterHelper.get(LedgerModel.NAME + "player", "id"), request.get("type"), request.get("time0"));
    }

    @Execute(name = "player-id", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object playerId() {
        return ledgerService.query(parameterHelper.get(LedgerModel.NAME + "player-id", new String[]{"player", "id"}),
                request.get("type0"), request.get("time0"));
    }

    @Execute(name = "water", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object water() {
        return ledgerService.water(request.get("time"));
    }

    @Execute(name = "user", permit = Permit.sign, validates = {
            @Validate(validator = UserService.VALIDATOR_SIGN)
    })
    public Object user() {
        return ledgerService.user();
    }
}