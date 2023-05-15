package org.lpw.photon.ctrl.upload;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface UploadService {
    /**
     * 前缀。
     */
    String PREFIX = "photon.ctrl.upload.";

    /**
     * 上传文件保存根路径。
     */
    String ROOT = "/upload/";

    /**
     * 处理多文件上传请求。
     *
     * @param content 上传数据。
     * @return 处理结果。
     */
    JSONArray uploads(String content);

    /**
     * 处理单个文件上传请求。
     *
     * @param map 参数集。
     * @return 处理结果。
     */
    JSONObject upload(Map<String, String> map);

    /**
     * 处理上传请求。
     *
     * @param uploadReaders 上传数据读取实例集。
     * @return 处理结果。
     * @throws IOException IO异常。
     */
    JSONArray uploads(List<UploadReader> uploadReaders) throws IOException;

    /**
     * 处理上传请求。
     *
     * @param uploadReader 上传数据读取实例。
     * @return 处理结果。
     * @throws IOException IO异常。
     */
    JSONObject upload(UploadReader uploadReader) throws IOException;

    /**
     * 删除上传的文件。
     *
     * @param key 上传key。
     * @param uri 文件URI地址。
     */
    void remove(String key, String uri);

    /**
     * 获取保存路径。
     *
     * @param contentType 文件类型。
     * @param name        归类。
     * @param suffix      文件后缀。
     * @return 文件路径。
     */
    String newSavePath(String contentType, String name, String suffix);

    /**
     * 获取Content-Type。
     *
     * @param uri URI。
     * @return Content-Type，失败则返回null。
     */
    String getContentType(String uri);
}
