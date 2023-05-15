package org.lpw.photon.aio;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextClosedListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component("photon.aio.servers")
public class AioServersImpl implements AioServers, ContextClosedListener {
    private Set<AioServer> set = Collections.synchronizedSet(new HashSet<>());

    @Override
    public AioServer get() {
        AioServer server = BeanFactory.getBean(AioServer.class);
        set.add(server);

        return server;
    }

    @Override
    public void close() {
        set.forEach(AioServer::close);
    }

    @Override
    public int getContextClosedSort() {
        return 6;
    }

    @Override
    public void onContextClosed() {
        close();
    }
}
