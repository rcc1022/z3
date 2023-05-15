package org.lpw.clivia.wps.file;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.context.Response;
import org.lpw.photon.ctrl.execute.Execute;
import org.lpw.photon.ctrl.template.Templates;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(FileModel.NAME + ".ctrl")
@Execute(name = "/wps/", key = FileModel.NAME, code = "157")
public class FileCtrl {
    @Inject
    private Header header;
    @Inject
    private Request request;
    @Inject
    private Response response;
    @Inject
    private FileService fileService;

    @Execute(name = "v1/3rd/file/info", permit = Permit.always, type = Templates.STRING)
    public Object info() {
        JSONObject object = fileService.info(header.get("x-weboffice-file-id"), request.getMap());
        if (object.containsKey("code"))
            response.sendError(422);

        return object.toJSONString();
    }

    @Execute(name = "v1/3rd/user/info", permit = Permit.always, type = Templates.STRING)
    public Object user() {
        JSONObject object = fileService.user(header.get("x-weboffice-file-id"), request.getMap(), request.getFromInputStream());
        if (object.containsKey("code"))
            response.sendError(422);

        return object.toJSONString();
    }

    @Execute(name = "v1/3rd/file/online", permit = Permit.always, type = Templates.STRING)
    public Object online() {
        return "";
    }
}
