package com.desert.eagle.robot;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(RobotModel.NAME + ".upload-listener")
public class UploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return RobotModel.NAME + ".avatar";
    }
}
