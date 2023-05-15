package org.lpw.clivia.increment;

import org.lpw.clivia.lock.LockHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(IncrementModel.NAME + ".service")
public class IncrementServiceImpl implements IncrementService {
    @Inject
    private LockHelper lockHelper;
    @Inject
    private IncrementDao incrementDao;

    @Override
    public int get(String key) {
        String lockId = lockHelper.lock(IncrementModel.NAME + ":" + key, 5000L, 5);
        if (lockId == null)
            return -1;

        IncrementModel increment = incrementDao.find(key);
        if (increment == null) {
            increment = new IncrementModel();
            increment.setKey(key);
        }
        increment.setValue(increment.getValue() + 1);
        incrementDao.save(increment);
        lockHelper.unlock(lockId);

        return increment.getValue();
    }
}
