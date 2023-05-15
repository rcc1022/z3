package org.lpw.photon.ctrl.upload;

import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UploadService.PREFIX + "ctrl")
@Execute(name = "/photon/ctrl/", key = "photon.ctrl")
public class UploadCtrl {
    @Inject
    private Request request;
    @Inject
    private UploadService uploadService;

    @Execute(name = "uploads")
    public Object uploads() {
        return uploadService.uploads(request.getFromInputStream());
    }

    @Execute(name = "upload")
    public Object upload() {
        return uploadService.upload(request.getMap());
    }
}
