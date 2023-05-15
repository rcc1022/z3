package org.lpw.photon.cache;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component("photon.cache")
public class CacheImpl implements Cache, ContextRefreshedListener {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Value("${photon.cache.name:}")
    private String name;
    private Map<String, Handler> handlers;

    @Override
    public void put(String key, Object value, boolean resident) {
        put(null, key, value, resident);
    }

    @Override
    public void put(String type, String key, Object value, boolean resident) {
        if (validator.isEmpty(key) || value == null)
            return;

        getHandler(type).put(key, value, resident);
    }

    @Override
    public <T> T get(String key) {
        return get(null, key);
    }

    @Override
    public <T> T get(String type, String key) {
        if (validator.isEmpty(key))
            return null;

        return getHandler(type).get(key);
    }

    @Override
    public <T> boolean has(String key, T value) {
        return has(null, key, value);
    }

    @Override
    public <T> boolean has(String type, String key, T value) {
        T v = get(type, key);

        return v != null && v.equals(value);
    }

    @Override
    public <T> T computeIfAbsent(String key, Function<String, T> function, boolean resident) {
        return computeIfAbsent(null, key, function, resident);
    }

    @Override
    public <T> T computeIfAbsent(String type, String key, Function<String, T> function, boolean resident) {
        T t = get(type, key);
        if (t == null) {
            t = function.apply(key);
            if (t == null)
                return null;

            put(key, t, resident);
        }

        return t;
    }

    @Override
    public <T> T remove(String key) {
        return remove(null, key);
    }

    @Override
    public <T> T remove(String type, String key) {
        if (validator.isEmpty(key))
            return null;

        return getHandler(null).remove(key);
    }

    private Handler getHandler(String name) {
        return handlers.get(validator.isEmpty(name) ? this.name : name);
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        handlers = new HashMap<>();
        BeanFactory.getBeans(Handler.class).forEach(handler -> handlers.put(handler.getName(), handler));

        if (logger.isInfoEnable())
            logger.info("初始化[{}]个缓存处理器[{}]。", handlers.size(), name);
    }
}
