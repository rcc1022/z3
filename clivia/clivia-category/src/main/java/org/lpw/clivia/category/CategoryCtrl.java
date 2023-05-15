package org.lpw.clivia.category;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(CategoryModel.NAME + ".ctrl")
@Execute(name = "/category/", key = CategoryModel.NAME, code = "102")
public class CategoryCtrl {
    @Inject
    private Request request;
    @Inject
    private CategoryService categoryService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return categoryService.query(request.get("key"), request.get("pointTo"));
    }

    @Execute(name = "faq-query", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 1),
            @Validate(validator = Validators.SIGN)
    })
    public Object faqQuery() {
        return categoryService.query(request.get("key"), null);
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 1),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "name", failureCode = 3),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 4),
            @Validate(validator = Validators.SIGN)
    })
    public Object save() {
        categoryService.save(request.setToModel(CategoryModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 11),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = CategoryService.VALIDATOR_LEAF, parameters = {"key", "id"}, failureCode = 12)
    })
    public Object delete() {
        categoryService.delete(request.get("id"));

        return "";
    }

    @Execute(name = "list", permit = Permit.always)
    public Object list() {
        return categoryService.query(request.get("key"), request.get("pointTo"));
    }
}
