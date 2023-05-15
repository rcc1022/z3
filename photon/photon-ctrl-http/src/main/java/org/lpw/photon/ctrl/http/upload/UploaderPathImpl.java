package org.lpw.photon.ctrl.http.upload;

import org.lpw.photon.ctrl.upload.UploadReader;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Service(UploadService.PREFIX + "uploader.path")
public class UploaderPathImpl implements Uploader {
    @Inject
    private org.lpw.photon.ctrl.upload.UploadService uploadService;

    @Override
    public String getName() {
        return UploadService.UPLOAD_PATH;
    }

    @Override
    public byte[] upload(List<UploadReader> readers) throws IOException {
        return uploadService.upload(readers.get(0)).getString("path").getBytes();
    }
}
