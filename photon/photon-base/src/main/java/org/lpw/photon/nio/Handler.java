package org.lpw.photon.nio;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;

import org.lpw.photon.util.Logger;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

/**
 * 监听处理器。
 */
public abstract class Handler extends ChannelHandlerAdapter {
    @Inject
    protected Logger logger;
    @Inject
    protected NioHelper nioHelper;
    private Map<String, ByteArrayOutputStream> outputStreamMap = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext context, Object message) throws Exception {
        String sessionId = nioHelper.getSessionId(context);
        ByteArrayOutputStream outputStream = outputStreamMap.get(sessionId);
        if (outputStream == null)
            outputStream = new ByteArrayOutputStream();
        byte[] bytes = nioHelper.read(message);
        outputStream.write(bytes, 0, bytes.length);
        outputStreamMap.put(sessionId, outputStream);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) throws Exception {
        String sessionId = nioHelper.getSessionId(context);
        ByteArrayOutputStream outputStream = outputStreamMap.get(sessionId);
        if (outputStream == null)
            return;

        outputStreamMap.remove(sessionId);
        outputStream.close();
        getListener().receive(sessionId, outputStream.toByteArray());
    }

    /**
     * 获取监听器。
     *
     * @return 监听器。
     */
    protected abstract Listener getListener();
}
