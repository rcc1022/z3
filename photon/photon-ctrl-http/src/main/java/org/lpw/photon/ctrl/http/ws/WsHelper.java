package org.lpw.photon.ctrl.http.ws;

import org.lpw.photon.ctrl.context.ResponseSender;

import javax.websocket.Session;

/**
 * WebSocket支持类。
 */
public interface WsHelper extends ResponseSender {
    /**
     * WebSocket监听URI地址。
     */
    String URI = "/photon/ctrl-http/ws";

    /**
     * 当前线程上下文IP地址key。
     */
    String IP = "photon.ctrl.http.ws.ip";

    /**
     * 当前线程上下文端口号key。
     */
    String PORT = "photon.ctrl.http.ws.port";

    /**
     * 处理连接打开事件。
     *
     * @param session 连接Session。
     */
    void open(Session session);

    /**
     * 处理接收到新消息事件。
     *
     * @param session 连接Session。
     * @param message 消息。
     */
    void message(Session session, String message);

    /**
     * 发送消息到客户端。
     *
     * @param sessionId 客户端Session ID。
     * @param message 消息。
     */
    void send(String sessionId, String message);

    /**
     * 发送消息到所有客户端。
     *
     * @param message 消息。
     */
    void send(String message);

    /**
     * 处理连接异常事件。
     *
     * @param session   连接Session。
     * @param throwable 异常。
     */
    void error(Session session, Throwable throwable);

    /**
     * 处理连接关闭事件。
     *
     * @param session 连接Session。
     */
    void close(Session session);

    /**
     * 关闭客户端连接。
     *
     * @param sessionId 客户端Session ID。
     */
    void close(String sessionId);
}
