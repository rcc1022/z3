package org.lpw.photon.ctrl.context.json;

import org.lpw.photon.ctrl.context.SessionAdapter;

public class JsonSessionAdapter implements SessionAdapter {
    private String sessionId;

    public JsonSessionAdapter(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public String getId() {
        return sessionId;
    }
}
