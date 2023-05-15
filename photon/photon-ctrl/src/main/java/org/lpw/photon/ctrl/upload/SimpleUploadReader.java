package org.lpw.photon.ctrl.upload;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.storage.Storage;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Http;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SimpleUploadReader implements UploadReader {
    private final String name;
    private String fileName;
    private String contentType;
    private final String base64;
    private final String string;
    private final String url;
    private final Map<String, String> map;
    private byte[] bytes;
    private InputStream inputStream;

    SimpleUploadReader(Map<String, String> map) {
        name = map.get("name");
        fileName = map.get("fileName");
        contentType = map.get("contentType");
        base64 = map.get("base64");
        string = map.get("string");
        url = map.get("url");
        Set<String> set = new HashSet<>();
        set.add("name");
        set.add("fileName");
        set.add("contentType");
        set.add("base64");
        set.add("string");
        set.add("url");
        this.map = new HashMap<>();
        map.keySet().stream().filter(key -> !set.contains(key)).forEach(key -> this.map.put(key, map.get(key)));
        read();
    }

    private void read() {
        if (base64 != null) {
            bytes = BeanFactory.getBean(Codec.class).decodeBase64(base64);

            return;
        }

        if (string != null) {
            bytes = string.getBytes();

            return;
        }

        if (url == null)
            return;

        Map<String, String> map = new HashMap<>();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BeanFactory.getBean(Http.class).get(url, null, null, map, outputStream);
        if (contentType == null && map.containsKey("Content-Type"))
            contentType = map.get("Content-Type");
        if (fileName == null) {
            String name = url.substring(url.lastIndexOf('/') + 1);
            if (name.indexOf('.') == -1 && contentType != null)
                fileName = contentType.replace('/', '.');
            else
                fileName = name.toLowerCase();
        }
        bytes = outputStream.toByteArray();
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
        return getBytes().length;
    }

    @Override
    public void write(Storage storage, String path) throws IOException {
        storage.write(path, getBytes());
    }

    @Override
    public InputStream getInputStream() {
        if (inputStream == null)
            inputStream = new ByteArrayInputStream(getBytes());

        return inputStream;
    }

    @Override
    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public void delete() throws IOException {
    }
}
