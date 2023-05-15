package org.lpw.photon.cache.lr;

import org.lpw.photon.nio.NioHelper;
import org.lpw.photon.nio.ServerListener;
import org.lpw.photon.util.Serializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("photon.cache.lr.listener")
public class ListenerImpl implements ServerListener {
    @Inject
    private Serializer serializer;
    @Inject
    private NioHelper nioHelper;
    @Inject
    private Local local;
    @Inject
    private Remote remote;
    @Value("${photon.cache.remote.port:0}")
    private int port;
    @Value("${photon.cache.remote.thread:5}")
    private int thread;

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public int getMaxThread() {
        return thread;
    }

    @Override
    public void accept(String sessionId) {
        nioHelper.send(sessionId, remote.getId().getBytes());
    }

    @Override
    public void receive(String sessionId, byte[] message) {
        if (message == null)
            return;

        Object object = serializer.unserialize(message);
        if (object == null)
            return;

        if (object instanceof String string)
            local.remove(string);
        else if (object instanceof Element element)
            local.put(element);
    }

    @Override
    public void disconnect(String sessionId) {
    }
}
