package org.lpw.photon.ctrl.http.upload;

import org.lpw.photon.ctrl.upload.UploadReader;

import java.io.IOException;
import java.util.List;

/**
 * 上传处理器。
 */
public interface Uploader {
    /**
     * 获取处理器名称。
     *
     * @return 处理器名称。
     */
    String getName();

    /**
     * 处理上传并返回输出数据。
     *
     * @param readers 上传读取器集。
     * @return 输出数据。
     * @throws IOException IO异常。
     */
    byte[] upload(List<UploadReader> readers) throws IOException;
}
