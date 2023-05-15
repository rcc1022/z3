package org.lpw.clivia.olcs.faq;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(FaqModel.NAME + ".ctrl")
@Execute(name = "/olcs/faq/", key = FaqModel.NAME, code = "162")
public class FaqCtrl {
    @Inject
    private Request request;
    @Inject
    private FaqService faqService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return faqService.query(request.get("subject"), request.get("content"), request.getAsInt("frequently", -1));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "subject", failureCode = 3),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        faqService.save(request.setToModel(FaqModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object delete() {
        faqService.delete(request.get("id"));

        return "";
    }
}