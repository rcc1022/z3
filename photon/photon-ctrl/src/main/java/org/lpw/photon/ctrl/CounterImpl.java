package org.lpw.photon.ctrl;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.ctrl.security.TrustfulIp;
import org.lpw.photon.storage.StorageListener;
import org.lpw.photon.storage.Storages;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Controller("photon.ctrl.counter")
public class CounterImpl implements Counter, StorageListener {
    private static final String CACHE_DELAY = "photon.ctrl.counter.delay:";

    @Inject
    private Cache cache;
    @Inject
    private Converter converter;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private TrustfulIp trustfulIp;
    @Value("${photon.ctrl.counter:/WEB-INF/security/counter.json}")
    private String path;
    private int max = 512;
    private int ipMax = 64;
    private int ipDelay = 5000;
    private Map<String, Integer> uriMap = new HashMap<>();
    private AtomicInteger counter = new AtomicInteger();
    private Map<String, AtomicInteger> ips = new ConcurrentHashMap<>();
    private Map<String, AtomicInteger> uris = new ConcurrentHashMap<>();

    @Override
    public boolean increase(String uri, String ip) {
        if (counter.incrementAndGet() >= max) {
            logger.warn(null, "超过最大并发处理数[{}]。", max);

            return false;
        }

        if (uriMap.containsKey(uri) && uris.computeIfAbsent(uri, u -> new AtomicInteger()).incrementAndGet() > uriMap.get(uri)) {
            logger.warn(null, "超过URI[{}]最大并发处理数[{}]。", uri, uriMap.get(uri));

            return false;
        }

        if (trustfulIp.contains(ip))
            return true;

        String key = CACHE_DELAY + ip;
        Long time = cache.get(key);
        if (time != null && System.currentTimeMillis() - time < ipDelay)
            return false;

        int n = ips.computeIfAbsent(ip, k -> new AtomicInteger()).incrementAndGet();
        if (n > ipMax) {
            cache.put(key, System.currentTimeMillis(), false);
            logger.warn(null, "超过IP[{}]最大并发处理数[{}]。", ip, ipMax);

            return false;
        }

        return true;
    }

    @Override
    public int get() {
        return counter.get();
    }

    @Override
    public void decrease(String uri, String ip) {
        counter.decrementAndGet();
        if (uris.containsKey(uri) && uris.get(uri).decrementAndGet() <= 0)
            uris.get(uri).set(0);
        if (trustfulIp.contains(ip))
            return;

        if (ips.containsKey(ip) && ips.get(ip).decrementAndGet() <= 0)
            ips.remove(ip);
    }

    @Override
    public int max() {
        return max;
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{path};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        JSONObject object = json.toObject(io.readAsString(absolutePath));
        max = object.getIntValue("max");
        ipMax = object.getJSONObject("ip").getIntValue("max");
        ipDelay = object.getJSONObject("ip").getIntValue("delay");
        Map<String, Integer> uris = new HashMap<>();
        JSONObject uri = object.getJSONObject("uri");
        uri.keySet().forEach(key -> uris.put(key, uri.getIntValue(key)));
        uriMap = uris;
        if (logger.isInfoEnable())
            logger.info("更新服务并发配置[{}:{}:{}:{}]。", max, ipMax, ipDelay, converter.toString(uriMap));
    }
}
