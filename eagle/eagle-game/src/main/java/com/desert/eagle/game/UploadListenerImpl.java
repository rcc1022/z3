package com.desert.eagle.game;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(GameModel.NAME + ".upload-listener")
public class UploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return GameModel.NAME;
    }
}
