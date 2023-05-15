package org.lpw.clivia.category;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(CategoryModel.NAME + ".upload-listener.image")
public class ImageUploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return CategoryModel.NAME + ".image";
    }
}
