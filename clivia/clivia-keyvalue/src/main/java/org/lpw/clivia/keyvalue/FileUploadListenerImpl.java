package org.lpw.clivia.keyvalue;

import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.springframework.stereotype.Controller;

@Controller(KeyvalueModel.NAME + ".upload-listener.file")
public class FileUploadListenerImpl implements UploadListener {
    @Override
    public String getKey() {
        return KeyvalueModel.NAME + ".file";
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return true;
    }
}
