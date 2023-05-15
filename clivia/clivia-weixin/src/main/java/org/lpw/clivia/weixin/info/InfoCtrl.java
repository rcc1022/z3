package org.lpw.clivia.weixin.info;

import javax.inject.Inject;

import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

@Controller(InfoModel.NAME + ".ctrl")
@Execute(name = "/weixin/info/", key = InfoModel.NAME, code = "155")
public class InfoCtrl {
    @Inject
    private InfoService infoService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN, string = {"clivia-weixin"})
    })
    public Object query() {
        return infoService.query();
    }
}
