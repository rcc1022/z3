package org.lpw.photon.ctrl;

public interface CtrlHelper {
    /**
     * 获取对外服务URL。
     *
     * @param uri 服务URI。
     * @return 对外服务URL，如果未配置根服务则返回null。
     */
    String url(String uri);
}
