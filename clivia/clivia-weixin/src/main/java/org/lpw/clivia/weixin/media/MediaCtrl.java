package org.lpw.clivia.weixin.media;

import org.lpw.clivia.weixin.WeixinService;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.validate.Validate;
import org.lpw.photon.ctrl.validate.Validators;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(MediaModel.NAME + ".ctrl")
@Execute(name = "/weixin/media/", key = MediaModel.NAME, code = "155")
public class MediaCtrl {
    @Inject
    private Request request;
    @Inject
    private MediaService mediaService;

    @Execute(name = "query", validates = {
            @Validate(validator = Validators.SIGN)
    })
    public Object query() {
        return mediaService.query(request.get("key"), request.get("appId"), request.get("type"), request.get("name"), request.get("time"));
    }

    @Execute(name = "save", validates = {
            @Validate(validator = Validators.NOT_EMPTY, parameter = "key", failureCode = 2),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "key", failureCode = 3),
            @Validate(validator = Validators.IN, string = {"image", "voice", "video", "thumb"}, parameter = "type", failureCode = 83),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "name", failureCode = 84),
            @Validate(validator = Validators.NOT_EMPTY, parameter = "uri", failureCode = 85),
            @Validate(validator = Validators.MAX_LENGTH, number = {100}, parameter = "uri", failureCode = 86),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = WeixinService.VALIDATOR_EXISTS, parameter = "key", failureCode = 52)
    })
    public Object create() {
        mediaService.create(request.setToModel(MediaModel.class));

        return "";
    }

    @Execute(name = "delete", validates = {
            @Validate(validator = Validators.ID, parameter = "id", failureCode = 81),
            @Validate(validator = Validators.SIGN),
            @Validate(validator = MediaService.VALIDATOR_EXISTS, parameter = "id", failureCode = 82)
    })
    public Object delete() {
        mediaService.delete(request.get("id"));

        return "";
    }
}
