package org.lpw.clivia.lock;

import org.lpw.photon.atomic.Atomicable;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Thread;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Service(LockModel.NAME + ".helper")
public class LockHelperImpl implements LockHelper, Atomicable, SecondsJob {
    @Inject
    private Digest digest;
    @Inject
    private Thread thread;
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private LockDao lockDao;
    @Value("${clivia.lock.type:0}")
    private int type;
    private final ThreadLocal<Set<String>> ids = new ThreadLocal<>();
    private final Map<String, List<LockModel>> map = new ConcurrentHashMap<>();
    private final Map<String, String> idmd5s = new ConcurrentHashMap<>();

    @Override
    public <T> T lock(String key, long wait, int alive, Function<String, T> function) {
        String id = lock(key, wait, alive);
        if (id == null)
            return null;

        T t = function.apply(id);
        unlock(id);

        return t;
    }

    @Override
    public String lock(String key, long wait, int alive) {
        if (key == null)
            return null;

        String md5 = digest.md5(key);
        LockModel lock = new LockModel();
        lock.setMd5(md5);
        lock.setKey(key);
        lock.setCreate(System.currentTimeMillis());
        lock.setExpire(lock.getCreate() + 1000L * (alive > 0 ? alive : 5));

        String lockId = type == 1 ? lockDb(md5, lock, wait) : lockMap(md5, lock, wait);
        if (lockId == null)
            return null;

        if (ids.get() == null)
            ids.set(new HashSet<>());
        ids.get().add(lockId);

        return lockId;
    }

    private String lockDb(String md5, LockModel lock, long wait) {
        lockDao.save(lock);
        for (long i = 0L; i < wait; i++) {
            LockModel model = lockDao.findByMd5(md5);
            if (model == null)
                return null;

            if (lock.getId().equals(model.getId()))
                return lock.getId();

            thread.sleep(1, TimeUnit.MilliSecond);
        }
        unlockDb(lock.getId());

        return null;
    }

    private String lockMap(String md5, LockModel lock, long wait) {
        lock.setId(generator.uuid());
        List<LockModel> list = map.computeIfAbsent(md5, key -> Collections.synchronizedList(new ArrayList<>()));
        list.add(lock);
        idmd5s.put(lock.getId(), md5);
        for (long i = 0L; i < wait; i++) {
            LockModel model = list.get(0);
            if (model == null)
                return null;

            if (lock.getId().equals(model.getId()))
                return lock.getId();

            thread.sleep(1, TimeUnit.MilliSecond);
        }
        unlockMap(lock.getId());

        return null;
    }

    @Override
    public void unlock(String id) {
        if (id == null)
            return;

        if (type == 1)
            unlockDb(id);
        else
            unlockMap(id);
        ids.get().remove(id);
    }

    private void unlockDb(String id) {
        lockDao.delete(id);
    }

    private void unlockMap(String id) {
        String md5 = idmd5s.remove(id);
        if (md5 == null || !map.containsKey(md5))
            return;

        List<LockModel> list = map.get(md5);
        for (int i = 0; i < list.size(); i++) {
            LockModel lock = list.get(i);
            if (lock.getId().equals(id)) {
                list.remove(i);

                break;
            }
        }
    }

    @Override
    public void fail(Throwable throwable) {
        close();
    }

    @Override
    public void close() {
        if (validator.isEmpty(ids.get()))
            return;

        Set<String> set = new HashSet<>(ids.get());
        set.forEach(this::unlock);
    }

    @Override
    public void executeSecondsJob() {
        long time = System.currentTimeMillis();
        if (type == 1) {
            lockDao.delete(time);
        } else {
            map.forEach((md5, list) -> {
                for (int i = list.size() - 1; i > -1; i--) {
                    if (list.get(i).getExpire() < time) {
                        list.remove(i);
                    }
                }
            });
        }
    }
}
