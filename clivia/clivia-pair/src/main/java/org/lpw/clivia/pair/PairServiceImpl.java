package org.lpw.clivia.pair;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONObject;

import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Service;

@Service(PairModel.NAME + ".service")
public class PairServiceImpl implements PairService {
    @Inject
    private DateTime dateTime;
    @Inject
    private Logger logger;
    @Inject
    private PairDao pairDao;

    @Override
    public int count(String owner) {
        return pairDao.count(owner);
    }

    @Override
    public int count(String owner, String value) {
        return pairDao.count(owner, value);
    }

    @Override
    public Set<String> values(String owner) {
        Set<String> set = new HashSet<>();
        pairDao.query(owner).getList().forEach(pair -> set.add(pair.getValue()));

        return set;
    }

    @Override
    public JSONObject query(String owner, boolean desc, int pageSize, int pageNum,
            Function<String, JSONObject> function) {
        return pairDao.query(owner, desc, pageSize, pageNum).toJson(pair -> function.apply(pair.getValue()));
    }

    @Override
    public void save(String owner, String value) {
        PairModel pair = pairDao.find(owner, value);
        if (pair == null) {
            pair = new PairModel();
            pair.setOwner(owner);
            pair.setValue(value);
        }
        pair.setTime(dateTime.now());
        try {
            pairDao.save(pair);
        } catch (Throwable throwable) {
            logger.warn(throwable, "保存paie数据[{}:{}]时发生异常！", owner, value);
        }
    }

    @Override
    public void delete(String owner, String value) {
        pairDao.delete(owner, value);
    }
}
