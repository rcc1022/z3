package org.lpw.photon.ctrl.http;

import javax.servlet.http.HttpServletRequest;

public interface HttpListener {
    /**
     * 监听HTTP请求。
     *
     * @param request HTTP请求。
     * @param uri     请求URI。
     */
    void onHttpService(HttpServletRequest request, String uri);
}
