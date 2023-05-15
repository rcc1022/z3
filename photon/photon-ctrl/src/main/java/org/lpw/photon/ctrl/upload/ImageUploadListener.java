package org.lpw.photon.ctrl.upload;

import org.lpw.photon.util.Image;

import javax.inject.Inject;

public abstract class ImageUploadListener implements UploadListener {
    @Inject
    protected Image image;

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return image.is(uploadReader.getContentType(), uploadReader.getFileName());
    }
}
