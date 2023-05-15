package org.lpw.clivia.user.upload;

import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(UploadModel.NAME + ".listener")
public class UploadListenerImpl implements UploadListener {
    @Override
    public String getKey() {
        return UploadModel.NAME;
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return true;
    }
}
