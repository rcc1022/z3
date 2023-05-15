package org.lpw.photon.ctrl.http.context;

import org.lpw.photon.ctrl.context.SessionAdapter;

public class SessionAdapterImpl implements SessionAdapter {
    protected String sessionId;

    public SessionAdapterImpl(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getId() {
        return sessionId;
    }
}
