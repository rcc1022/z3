package org.lpw.photon.cache.lr;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextClosedListener;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.nio.NioHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.storage.StorageListener;
import org.lpw.photon.storage.Storages;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Serializer;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component("photon.cache.lr.remote")
public class RemoteImpl implements Remote, MinuteJob, StorageListener, ContextRefreshedListener, ContextClosedListener {
    @Inject
    private Validator validator;
    @Inject
    private Converter converter;
    @Inject
    private Numeric numeric;
    @Inject
    private Generator generator;
    @Inject
    private Serializer serializer;
    @Inject
    private Logger logger;
    @Inject
    private NioHelper nioHelper;
    @Value("${photon.cache.remote.port:0}")
    private int port;
    @Value("${photon.cache.remote.thread:5}")
    private int thread;
    @Value("${photon.cache.remote.ips:/WEB-INF/remote-cache}")
    private String ips;
    private String id;
    private Map<String, Channel> channels;
    private ExecutorService executorService;

    @Override
    public String getId() {
        if (id == null)
            id = generator.uuid();

        return id;
    }

    @Override
    public void put(Element element) {
        write(element);
    }

    @Override
    public void remove(String key) {
        write(key);
    }

    private void write(Object object) {
        if (validator.isEmpty(channels))
            return;

        channels.values().stream().filter((channel) -> channel.getState() == Channel.State.Connected)
                .forEach((channel) -> executorService.execute(() -> nioHelper.send(channel.getSessionId(), serializer.serialize(object))));
    }

    @Override
    public void executeMinuteJob() {
        if (validator.isEmpty(channels))
            return;

        channels.values().stream().filter((channel) -> channel.getState() == Channel.State.Disconnect)
                .forEach((channel) -> executorService.execute(channel::connect));
    }

    @Override
    public String getStorageType() {
        return Storages.TYPE_DISK;
    }

    @Override
    public String[] getScanPathes() {
        return new String[]{ips};
    }

    @Override
    public void onStorageChanged(String path, String absolutePath) {
        try {
            Map<String, Channel> map = new HashMap<>();
            BufferedReader reader = new BufferedReader(new FileReader(absolutePath));
            for (String line; (line = reader.readLine()) != null; ) {
                line = line.trim();
                if (line.length() == 0 || line.indexOf('#') == 0)
                    continue;

                int indexOf = line.indexOf(':');
                String ip = indexOf == -1 ? line : line.substring(0, indexOf);
                int port = indexOf == -1 ? this.port : numeric.toInt(line.substring(indexOf + 1));
                if (port < 1)
                    continue;

                String key = ip + ":" + port;
                if (channels.get(key) != null) {
                    map.put(key, channels.get(key));

                    continue;
                }

                Channel channel = BeanFactory.getBean(Channel.class);
                channel.set(ip, port);
                map.put(key, channel);
            }
            reader.close();
            channels = map;

            if (logger.isInfoEnable())
                logger.info("设置远程缓存地址[{}]。", converter.toString(channels.keySet()));
        } catch (Exception e) {
            logger.warn(e, "解析远程缓存配置[{}:{}]时发生异常！", path, absolutePath);
        }
    }

    @Override
    public int getContextRefreshedSort() {
        return 5;
    }

    @Override
    public void onContextRefreshed() {
        if (executorService == null)
            executorService = Executors.newFixedThreadPool(thread);
    }

    @Override
    public int getContextClosedSort() {
        return 5;
    }

    @Override
    public void onContextClosed() {
        if (executorService != null)
            executorService.shutdown();
    }
}
