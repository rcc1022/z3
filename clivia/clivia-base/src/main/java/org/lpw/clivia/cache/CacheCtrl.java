package org.lpw.clivia.cache;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller("clivia.cache.ctrl")
@Execute(name = "/cache/")
public class CacheCtrl {
    @Inject
    private Request request;
    @Inject
    private CacheService cacheService;

    @Execute(name = "remove", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object remove() {
        return cacheService.remove(request.get("type"), request.get("key"));
    }
}
