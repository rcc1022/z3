package org.lpw.photon.storage;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("photon.storages")
public class StoragesImpl implements Storages, ContextRefreshedListener, SecondsJob {
    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    @Value("${photon.storage.default:disk}")
    private String type;
    private Map<String, Storage> storages;
    private Map<String, String> types;
    private Map<String, String> pathes;
    private Map<String, StorageListener> listeners;
    private Map<String, Long> times;

    @Override
    public Storage get() {
        return storages.get(type);
    }

    @Override
    public Storage get(String type) {
        return storages.get(validator.isEmpty(type) ? this.type : type);
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        storages = new HashMap<>();
        BeanFactory.getBeans(Storage.class).forEach(storage -> storages.put(storage.getType(), storage));
        Collection<StorageListener> listeners = BeanFactory.getBeans(StorageListener.class);
        if (validator.isEmpty(listeners))
            return;

        types = new HashMap<>();
        pathes = new HashMap<>();
        this.listeners = new HashMap<>();
        times = new HashMap<>();
        listeners.forEach(listener -> {
            if (validator.isEmpty(listener.getScanPathes()))
                return;

            for (String path : listener.getScanPathes()) {
                if (types.containsKey(path))
                    logger.warn(null, "监听路径[{}]已存在！", path);

                types.put(path, validator.isEmpty(listener.getStorageType()) ? type : listener.getStorageType());
                pathes.put(path, get(types.get(path)).getAbsolutePath(path));
                this.listeners.put(path, listener);

                if (logger.isDebugEnable())
                    logger.debug("启动文件[{}]变化监听。", path);
            }
        });
    }

    @Override
    public void executeSecondsJob() {
        if (types == null)
            return;

        types.forEach((path, type) -> {
            String absolutePath = pathes.get(path);
            if (validator.isEmpty(absolutePath) || !get(type).exists(absolutePath))
                return;

            long time = get(type).lastModified(absolutePath);
            Long cacheTime = times.get(path);
            if (cacheTime != null && cacheTime >= time)
                return;

            times.put(path, time);
            listeners.get(path).onStorageChanged(path, absolutePath);
        });
    }
}
