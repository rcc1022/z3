package org.lpw.photon.nio;

/**
 * 服务器。用于管理端口监听。
 */
public interface Server {
    /**
     * 启动服务。
     *
     * @param listener 监听器。
     */
    void listen(ServerListener listener);

    /**
     * 关闭服务。
     */
    void close();
}
