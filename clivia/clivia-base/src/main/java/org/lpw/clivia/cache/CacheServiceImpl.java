package org.lpw.clivia.cache;

import org.lpw.photon.cache.Cache;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("clivia.cache.service")
public class CacheServiceImpl implements CacheService {
    @Inject
    private Cache cache;

    @Override
    public boolean remove(String type, String key) {
        return cache.remove(type, key) != null;
    }
}
