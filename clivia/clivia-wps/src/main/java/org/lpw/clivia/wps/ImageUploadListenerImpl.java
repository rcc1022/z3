package org.lpw.clivia.wps;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(WpsModel.NAME + ".upload-listener.image")
public class ImageUploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return WpsModel.NAME + ".image";
    }
}
