package com.desert.eagle.domain;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(DomainModel.NAME + ".ctrl")
@Execute(name = "/domain/", key = DomainModel.NAME, code = "252")
public class DomainCtrl {
    @Inject
    private Request request;
    @Inject
    private DomainService domainService;

    @Execute(name = "query0,query1,query2,query3", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return domainService.query(request.getAsInt("type"));
    }

    @Execute(name = "creates", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.SIGN)
    })
    public Object creates() {
        domainService.creates(request.getAsInt("type"), request.getAsArray("names"), request.getAsInt("status"));

        return "";
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        domainService.save(request.setToModel(DomainModel.class));

        return "";
    }

    @Execute(name = "status", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object status() {
        domainService.status(request.get("id"), request.getAsInt("status"));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        domainService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "entrance", permit = Permit.always)
    public Object entrance() {
        return domainService.entrance();
    }

    @Execute(name = "video", permit = Permit.always)
    public Object video() {
        return domainService.video();
    }
}