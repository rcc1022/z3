package org.lpw.photon.ctrl.socket;

import org.lpw.photon.ctrl.Handler;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.nio.NioHelper;
import org.lpw.photon.util.Compresser;
import org.lpw.photon.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller("photon.socket.helper")
public class SocketHelperImpl implements SocketHelper {
    @Inject
    private Compresser compresser;
    @Inject
    private Logger logger;
    @Inject
    private NioHelper nioHelper;
    @Inject
    private Handler handler;
    @Inject
    private Session session;
    @Value("${photon.ctrl.socket.zip-size:4096}")
    private int zipSize;
    private Map<String, String> tsids = new ConcurrentHashMap<>();
    private Map<String, String> sids = new ConcurrentHashMap<>();

    @Override
    public void bind(String sessionId, String photonSessionId) {
        tsids.put(photonSessionId, sessionId);
        sids.put(sessionId, photonSessionId);
    }

    @Override
    public void send(String sessionId, ByteArrayOutputStream byteArrayOutputStream) {
        send(sessionId, byteArrayOutputStream.toByteArray());
    }

    @Override
    public void send(String sessionId, byte[] message) {
        if (sessionId == null)
            sessionId = session.getId();
        if (tsids.containsKey(sessionId))
            sessionId = tsids.get(sessionId);
        try {
            write(sessionId, message);
        } catch (IOException e) {
            nioHelper.close(sessionId);
            logger.warn(e, "发送数据到客户端[{}]时发生异常！", sessionId);
        }
    }

    private void write(String sessionId, byte[] message) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        if (message.length <= zipSize)
            write(dataOutputStream, 0, message);
        else
            write(dataOutputStream, message.length, compresser.zip(message));
        dataOutputStream.close();
        outputStream.close();
        nioHelper.send(sessionId, outputStream.toByteArray());
    }

    private void write(DataOutputStream dataOutputStream, int unzipLength, byte[] message) throws IOException {
        dataOutputStream.write(message.length + 8);
        dataOutputStream.write(unzipLength);
        dataOutputStream.write(message, 0, message.length);
    }

    @Override
    public void unbind(String sessionId, String photonSessionId) {
        if (sessionId != null) {
            String tsid = sids.remove(sessionId);
            if (tsid != null) {
                tsids.remove(tsid);
                handler.clear(tsid);
            }
            handler.clear(sessionId);
        }

        if (photonSessionId != null) {
            String sid = tsids.remove(photonSessionId);
            if (sid != null) {
                sids.remove(sid);
                handler.clear(sid);
                nioHelper.close(sid);
            }
            handler.clear(photonSessionId);
        }
    }
}
