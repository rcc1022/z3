package org.lpw.clivia.shortcut;

import org.lpw.photon.cache.Cache;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ShortcutModel.NAME + ".service")
public class ShortcutServiceImpl implements ShortcutService {
    private static final String CACHE_CODE = ShortcutModel.NAME + ".service.code:";

    @Inject
    private Cache cache;
    @Inject
    private Digest digest;
    @Inject
    private Generator generator;
    @Inject
    private DateTime dateTime;
    @Inject
    private ShortcutDao shortcutDao;

    @Override
    public String find(String code) {
        String cacheKey = CACHE_CODE + code;
        String value = cache.get(cacheKey);
        if (value == null) {
            ShortcutModel shortcut = shortcutDao.find(code);
            if (shortcut != null)
                cache.put(cacheKey, value = shortcut.getValue(), false);
        }

        return value;
    }

    @Override
    public String save(int length, String value) {
        String md5 = digest.md5(value);
        ShortcutModel shortcut = shortcutDao.find(md5, length);
        if (shortcut != null)
            return shortcut.getCode();

        String code = generate(length);
        if (code == null)
            return null;

        shortcut = new ShortcutModel();
        shortcut.setCode(code);
        shortcut.setMd5(md5);
        shortcut.setLength(length);
        shortcut.setValue(value);
        shortcut.setTime(dateTime.now());
        shortcutDao.save(shortcut);

        return code;
    }

    private String generate(int length) {
        for (int i = 0; i < 1024; i++) {
            String code = generator.random(length);
            if (shortcutDao.find(code) == null)
                return code;
        }

        return null;
    }
}
