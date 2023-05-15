package org.lpw.photon.ctrl.http.context;

import org.lpw.photon.ctrl.context.HeaderAdapter;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class HeaderAdapterImpl implements HeaderAdapter {
    protected HttpServletRequest request;

    public HeaderAdapterImpl(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String get(String name) {
        return request.getHeader(name);
    }

    @Override
    public String getIp() {
        return request.getRemoteAddr();
    }

    @Override
    public Map<String, String> getMap() {
        Map<String, String> map = new HashMap<>();
        for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements(); ) {
            String name = names.nextElement();
            map.put(name, request.getHeader(name));
        }

        return map;
    }
}
