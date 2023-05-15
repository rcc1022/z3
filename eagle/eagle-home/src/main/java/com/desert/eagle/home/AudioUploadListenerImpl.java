package com.desert.eagle.home;

import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.springframework.stereotype.Controller;

@Controller(HomeModel.NAME + ".upload-listener.audio")
public class AudioUploadListenerImpl implements UploadListener {
    @Override
    public String getKey() {
        return HomeModel.NAME + ".audio";
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return true;
    }
}
