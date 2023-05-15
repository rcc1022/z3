package org.lpw.clivia.weixin.reply;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(ReplyModel.NAME + ".upload-listener.image")
public class ImageUploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return ReplyModel.NAME + ".image";
    }
}
