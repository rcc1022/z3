package org.lpw.clivia.api;

import javax.inject.Inject;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

@Controller(ApiModel.NAME + ".ctrl")
@Execute(name = "/api/", key = ApiModel.NAME, code = "192")
public class ApiCtrl {
    @Inject
    private ApiService apiService;

    @Execute(name = "get", permit = Permit.always)
    public Object get() {
        return apiService.get();
    }
}
