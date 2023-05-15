package org.lpw.photon.aio;

/**
 * AIO服务器集。
 */
public interface AioServers {
    /**
     * 获取一个新的AIO服务器实例。
     *
     * @return 新的AIO服务器实例。
     */
    AioServer get();

    /**
     * 关闭所有AIO服务器。
     */
    void close();
}
