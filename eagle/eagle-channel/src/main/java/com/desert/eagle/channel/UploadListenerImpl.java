package com.desert.eagle.channel;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Service;

@Service(ChannelModel.NAME + ".upload-listener")
public class UploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return ChannelModel.NAME;
    }
}
