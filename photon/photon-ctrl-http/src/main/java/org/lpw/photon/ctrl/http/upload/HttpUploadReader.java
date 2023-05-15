package org.lpw.photon.ctrl.http.upload;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.ctrl.upload.UploadReader;
import org.lpw.photon.storage.Storage;
import org.lpw.photon.util.Io;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HttpUploadReader implements UploadReader {
    private Part part;
    private String name;
    private String fileName;
    private String contentType;
    private long size;
    private InputStream inputStream;
    private byte[] bytes;
    private Map<String, String> map;

    HttpUploadReader(Part part, Map<String, String> map) throws IOException {
        this.part = part;
        name = part.getName();
        fileName = part.getSubmittedFileName();
        contentType = part.getContentType();
        size = part.getSize();
        inputStream = part.getInputStream();
        this.map = map;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public String getParameter(String name) {
        return map.get(name);
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public void write(Storage storage, String path) throws IOException {
        storage.write(path, inputStream);
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public byte[] getBytes() {
        if (bytes == null)
            bytes = BeanFactory.getBean(Io.class).read(inputStream);

        return bytes;
    }

    @Override
    public void delete() throws IOException {
        part.delete();
    }
}
