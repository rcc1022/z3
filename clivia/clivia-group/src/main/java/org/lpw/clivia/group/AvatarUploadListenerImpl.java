package org.lpw.clivia.group;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(GroupModel.NAME + ".upload-listener.avatar")
public class AvatarUploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return GroupModel.NAME + ".avatar";
    }
}
