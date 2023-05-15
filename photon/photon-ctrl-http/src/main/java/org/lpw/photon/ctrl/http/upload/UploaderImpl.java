package org.lpw.photon.ctrl.http.upload;

import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.Json;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.IOException;
import java.util.List;

@Service(UploadService.PREFIX + "uploader")
public class UploaderImpl implements Uploader {
    @Inject
    private Json json;
    @Inject
    private org.lpw.photon.ctrl.upload.UploadService uploadService;

    @Override
    public String getName() {
        return UploadService.UPLOAD;
    }

    @Override
    public byte[] upload(List<UploadReader> readers) throws IOException {
        if (readers.size() == 1)
            return json.toBytes(uploadService.upload(readers.get(0)));

        return json.toBytes(uploadService.uploads(readers));
    }
}
