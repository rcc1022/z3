package org.lpw.clivia.faq;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(FaqModel.NAME + ".ctrl")
@Execute(name = "/faq/", key = FaqModel.NAME, code = "113")
public class FaqCtrl {
    @Inject
    private Request request;
    @Inject
    private FaqService faqService;

    @Execute(name = "query", permit = Permit.always)
    public Object query() {
        return faqService.query(request.get("key"));
    }

    @Execute(name = "get", permit = Permit.always)
    public Object get() {
        return faqService.get(request.get("id"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = { 100 }, parameter = "key", failureCode = 4),
            @Validate(validator = Validators.MAX_LENGTH, number = { 100 }, parameter = "subject", failureCode = 5),
            @Validate(validator = Validators.SIGN) })
    public Object save() {
        faqService.save(request.setToModel(FaqModel.class));

        return "";
    }

    @Execute(name = "delete", validates = { @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN) })
    public Object delete() {
        faqService.delete(request.get("id"));

        return "";
    }
}