package org.lpw.photon.ctrl.http.ws;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.bean.ContextClosedListener;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.ctrl.Dispatcher;
import org.lpw.photon.ctrl.context.HeaderAware;
import org.lpw.photon.ctrl.context.RequestAware;
import org.lpw.photon.ctrl.context.ResponseAware;
import org.lpw.photon.ctrl.context.SessionAware;
import org.lpw.photon.ctrl.context.json.JsonHeaderAdapter;
import org.lpw.photon.ctrl.context.json.JsonRequestAdapter;
import org.lpw.photon.ctrl.context.json.JsonResponseAdapter;
import org.lpw.photon.ctrl.context.json.JsonSessionAdapter;
import org.lpw.photon.ctrl.http.ServiceHelper;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.websocket.Session;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service("photon.ctrl.http.ws.helper")
public class WsHelperImpl implements WsHelper, ContextClosedListener {
    @Inject
    private Context context;
    @Inject
    private Generator generator;
    @Inject
    private Digest digest;
    @Inject
    private Json json;
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Inject
    private HeaderAware headerAware;
    @Inject
    private SessionAware sessionAware;
    @Inject
    private RequestAware requestAware;
    @Inject
    private ResponseAware responseAware;
    @Inject
    private Dispatcher dispatcher;
    private Map<String, Session> sessions = new ConcurrentHashMap<>();
    private Map<String, String> sids = new ConcurrentHashMap<>();
    private Map<String, String> ips = new ConcurrentHashMap<>();
    private int port;
    private String sessionKey;

    @Override
    public void open(Session session) {
        String sid = getSessionId(session);
        sessions.put(sid, session);
        ips.put(sid, context.getThreadLocal(IP));
        if (port == 0)
            port = context.getThreadLocal(PORT);
        if (logger.isDebugEnable())
            logger.debug("新的WebSocket连接[{}:{}:{}]。", port, ips.get(sid), sid);
    }

    @Override
    public void message(Session session, String message) {
        JSONObject object = json.toObject(message);
        if (object == null) {
            close(session);
            logger.warn(null, "收到不可处理的WebSocket数据[{}]。", message);

            return;
        }

        String sid = getSessionId(session);
        JSONObject header = object.getJSONObject("header");
        String tsid = null;
        if (!validator.isEmpty(header) && header.containsKey(ServiceHelper.SESSION_ID)) {
            tsid = header.getString(ServiceHelper.SESSION_ID);
            sids.put(sid, tsid);
            sids.put(tsid, sid);
        }

        headerAware.set(new JsonHeaderAdapter(header, ips.get(sid)));
        sessionAware.set(new JsonSessionAdapter(tsid == null ? sid : tsid));
        requestAware.set(new JsonRequestAdapter(port, object.getString("id"), object.getString("uri"),
                object.getJSONObject("request")));
        responseAware.set(new JsonResponseAdapter(this, sid));
        dispatcher.execute();
    }

    @Override
    public void send(String sessionId, ByteArrayOutputStream byteArrayOutputStream) {
        send(sessionId, byteArrayOutputStream.toString());
    }

    @Override
    public void send(String sessionId, String message) {
        if (validator.isEmpty(sessionId))
            return;

        if (!sessions.containsKey(sessionId))
            sessionId = sids.get(sessionId);
        if (!sessions.containsKey(sessionId))
            return;

        try {
            sessions.get(sessionId).getBasicRemote().sendText(message);
        } catch (Throwable throwable) {
            logger.warn(throwable, "推送消息[{}]到WebSocket客户端时发生异常！", message);
        }
    }

    @Override
    public void send(String message) {
        sessions.keySet().forEach(sid -> send(sid, message));
    }

    @Override
    public void error(Session session, Throwable throwable) {
        close(session);
    }

    @Override
    public void close(Session session) {
        close(getSessionId(session));
    }

    private String getSessionId(Session session) {
        if (sessionKey == null)
            sessionKey = generator.random(32);

        return digest.md5(sessionKey + session.getId());
    }

    @Override
    public void close(String sessionId) {
        Session session = sessions.remove(sessionId);
        String tsid = sids.remove(sessionId);
        if (tsid != null)
            sids.remove(tsid);
        ips.remove(sessionId);
        if (session == null)
            return;

        try {
            session.close();
        } catch (IOException e) {
            logger.warn(e, "关闭WebSocket Session[{}]时发生异常！", sessionId);
        }
    }

    @Override
    public int getContextClosedSort() {
        return 18;
    }

    @Override
    public void onContextClosed() {
        sessions.keySet().forEach(this::close);
    }
}
