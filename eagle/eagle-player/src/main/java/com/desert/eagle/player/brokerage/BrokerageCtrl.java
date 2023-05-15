package com.desert.eagle.player.brokerage;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(BrokerageModel.NAME + ".ctrl")
@Execute(name = "/player/brokerage/", key = BrokerageModel.NAME, code = "201")
public class BrokerageCtrl {
    @Inject
    private Request request;
    @Inject
    private BrokerageService brokerageService;
}