package org.lpw.clivia.push;

import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.springframework.stereotype.Controller;

@Controller(PushModel.NAME + ".upload-listener.cert")
public class CertUploadListenerImpl implements UploadListener {
    @Override
    public String getKey() {
        return PushModel.NAME + ".cert";
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return true;
    }
}
