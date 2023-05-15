package org.lpw.clivia.editor;

import org.lpw.photon.ctrl.upload.ImageUploadListener;
import org.springframework.stereotype.Controller;

@Controller(EditorModel.NAME + ".upload-listener.image")
public class ImageUploadListenerImpl extends ImageUploadListener {
    @Override
    public String getKey() {
        return EditorModel.NAME + ".image";
    }
}
