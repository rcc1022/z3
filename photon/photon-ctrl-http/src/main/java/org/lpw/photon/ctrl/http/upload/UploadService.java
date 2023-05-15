package org.lpw.photon.ctrl.http.upload;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UploadService {
    /**
     * 前缀。
     */
    String PREFIX = "photon.ctrl.http.upload.";
    /**
     * 上传Servlet URI地址。
     */
    String UPLOAD = "/photon/ctrl-http/upload";
    /**
     * 上传Servlet URI地址。
     */
    String UPLOAD_PATH = "/photon/ctrl-http/upload-path";

    /**
     * 上传文件。
     *
     * @param request  请求HttpServletRequest信息。
     * @param response 输出HttpServletResponse信息。
     * @param uploader 上传器名称。
     */
    void upload(HttpServletRequest request, HttpServletResponse response, String uploader);
}
