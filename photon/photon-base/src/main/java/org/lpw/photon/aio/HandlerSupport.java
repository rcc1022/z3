package org.lpw.photon.aio;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.util.Logger;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;

class HandlerSupport {
    @Inject
    Logger logger;
    @Inject
    AioHelper aioHelper;
    int port;

    void read(AsynchronousSocketChannel socketChannel, String sessionId, AioListener listener) {
        ByteBuffer buffer = aioHelper.getBuffer(sessionId);
        buffer.clear();
        socketChannel.read(buffer, null, BeanFactory.getBean(ReceiveHandler.class).bind(socketChannel, buffer, sessionId, listener));
    }
}
