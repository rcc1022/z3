package org.lpw.clivia.weixin.media;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(MediaModel.NAME + ".upload-listener")
public class UploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return MediaModel.NAME;
    }
}
