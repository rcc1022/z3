package org.lpw.photon.ctrl.upload;

import com.alibaba.fastjson.JSONObject;

/**
 * 上传拦截器。
 */
public interface UploadInterceptor {
    /**
     * 是否允许上传。
     *
     * @param reader      读取器。
     * @param listener    监听器。
     * @param contentType 文件类型。
     * @return 允许则返回true；否则返回false。
     */
    boolean enable(UploadReader reader, UploadListener listener, String contentType);

    /**
     * 上传完成。
     *
     * @param reader      读取器。
     * @param listener    监听器。
     * @param contentType 文件类型。
     * @param object      处理结果。
     */
    void complete(UploadReader reader, UploadListener listener, String contentType, JSONObject object);
}
