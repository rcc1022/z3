package com.desert.eagle.home;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.upload.UploadListener;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Io;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Controller(HomeModel.NAME + ".upload-listener.file")
public class FileUploadListenerImpl implements UploadListener {
    @Inject
    private Context context;
    @Inject
    private Io io;

    @Override
    public String getKey() {
        return HomeModel.NAME + ".file";
    }

    @Override
    public JSONObject settle(UploadReader uploadReader) throws IOException {
        String path = "/" + uploadReader.getFileName();
        JSONObject object = new JSONObject();
        object.put("fileName", uploadReader.getFileName());
        object.put("fileSize", uploadReader.getSize());
        object.put("name", getKey());
        try (OutputStream outputStream = new FileOutputStream(context.getAbsolutePath(path))) {
            io.copy(uploadReader.getInputStream(), outputStream);
            object.put("path", path);
            object.put("success", true);
        } catch (Throwable throwable) {
            object.put("success", false);
        }

        return object;
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        return uploadReader.getContentType().equals("text/plain") && uploadReader.getFileName().endsWith(".txt");
    }
}
