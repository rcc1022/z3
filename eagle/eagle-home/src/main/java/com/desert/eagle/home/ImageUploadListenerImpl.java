package com.desert.eagle.home;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(HomeModel.NAME + ".upload-listener.image")
public class ImageUploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return HomeModel.NAME + ".image";
    }
}
