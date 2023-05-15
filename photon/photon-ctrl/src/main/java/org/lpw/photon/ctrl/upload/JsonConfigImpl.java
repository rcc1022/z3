package org.lpw.photon.ctrl.upload;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class JsonConfigImpl implements JsonConfig {
    private final Map<Pattern, String> map;
    private final int[] imageSize;
    private long lastModify;

    JsonConfigImpl() {
        map = new HashMap<>();
        imageSize = new int[2];
    }

    @Override
    public String getKey() {
        return null;
    }

    @Override
    public void addPath(String contentType, String path) {
        map.put(Pattern.compile(contentType), path);
    }

    @Override
    public boolean isUploadEnable(UploadReader uploadReader) {
        for (Pattern pattern : map.keySet())
            if (pattern.matcher(uploadReader.getContentType()).find())
                return true;

        return false;
    }

    @Override
    public void setImageSize(int width, int height) {
        imageSize[0] = width;
        imageSize[1] = height;
    }

    @Override
    public long getLastModify() {
        return lastModify;
    }

    @Override
    public String getPath(UploadReader uploadReader) {
        for (Pattern pattern : map.keySet())
            if (pattern.matcher(uploadReader.getContentType()).find())
                return map.get(pattern);

        return null;
    }

    @Override
    public void setLastModify(long lastModify) {
        this.lastModify = lastModify;
    }

    @Override
    public int[] getImageSize() {
        return imageSize;
    }
}
