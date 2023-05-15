package org.lpw.clivia.async;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(AsyncModel.NAME + ".ctrl")
@Execute(name = "/async/")
public class AsyncCtrl {
    @Inject
    private Request request;
    @Inject
    private AsyncService asyncService;

    @Execute(name = "find")
    public Object find() {
        return asyncService.find(request.get("id"));
    }
}
