package org.lpw.clivia.upgrader;

import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.springframework.stereotype.Controller;

@Controller(UpgraderModel.NAME + "upload-listener")
public class UploadListenerImpl implements UploadListener {
    @Override
    public String getKey() {
        return UpgraderModel.NAME + ".file";
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return true;
    }
}
