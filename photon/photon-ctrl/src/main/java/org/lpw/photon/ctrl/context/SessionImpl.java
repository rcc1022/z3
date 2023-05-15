package org.lpw.photon.ctrl.context;

import org.lpw.photon.cache.Cache;
import org.lpw.photon.util.Context;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller("photon.ctrl.context.session")
public class SessionImpl implements Session, SessionAware {
    private static final String ADAPTER = "photon.ctrl.context.session.adapter";
    private static final String CACHE = "photon.ctrl.context.session:";

    @Inject
    private Context context;
    @Inject
    private Cache cache;

    @Override
    public void set(String key, Object value) {
        set(getId(), key, value);
    }

    @Override
    public void set(String id, String key, Object value) {
        String cacheKey = getCacheKey(id, key);
        context.putThreadLocal(cacheKey, value);
        cache.put(cacheKey, value, false);
    }

    @Override
    public <T> T get(String key) {
        return get(getId(), key);
    }

    @Override
    public <T> T get(String id, String key) {
        String cacheKey = getCacheKey(id, key);
        T value = context.getThreadLocal(cacheKey);

        return value == null ? cache.get(cacheKey) : value;
    }

    @Override
    public <T> T remove(String key) {
        return remove(getId(), key);
    }

    @Override
    public <T> T remove(String id, String key) {
        String cacheKey = getCacheKey(id, key);
        context.removeThreadLocal(cacheKey);

        return cache.remove(cacheKey);
    }

    private String getCacheKey(String id, String key) {
        return CACHE + id + key;
    }

    @Override
    public String getId() {
        SessionAdapter adapter = context.getThreadLocal(ADAPTER);

        return adapter == null ? null : adapter.getId();
    }

    @Override
    public void set(SessionAdapter adapter) {
        context.putThreadLocal(ADAPTER, adapter);
    }
}
