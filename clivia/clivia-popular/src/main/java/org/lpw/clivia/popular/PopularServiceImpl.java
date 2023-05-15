package org.lpw.clivia.popular;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(PopularModel.NAME + ".service")
public class PopularServiceImpl implements PopularService, MinuteJob {
    private static final String CACHE_PUBLISH = PopularModel.NAME + ".publish:";

    @Inject
    private Validator validator;
    @Inject
    private Cache cache;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private PopularDao popularDao;
    private List<String[]> list = Collections.synchronizedList(new ArrayList<>());

    @Override
    public JSONObject query(String key, int state) {
        return state == -1 ? popularDao.query(key, pagination.getPageSize(20), pagination.getPageNum()).toJson() :
                popularDao.query(key, state, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray publish(String key, int size) {
        String cacheKey = CACHE_PUBLISH + key + ":" + size + ":" + System.currentTimeMillis() / TimeUnit.Minute.getTime();
        JSONArray array = cache.get(cacheKey);
        if (array == null)
            cache.put(cacheKey, array = modelHelper.toJson(popularDao.query(key, 0, size, 0).getList()), false);

        return array;
    }

    @Override
    public void increase(String key, String value) {
        if (!validator.isEmpty(value))
            list.add(new String[]{key, value});
    }

    @Override
    public void increase(String key, String[] values) {
        if (validator.isEmpty(values))
            return;

        for (String value : values)
            increase(key, value);
    }

    @Override
    public void state(String id, int state) {
        PopularModel popular = popularDao.findById(id);
        popular.setState(state);
        popularDao.save(popular);
    }

    @Override
    public void executeMinuteJob() {
        if (list.isEmpty())
            return;

        Map<String, PopularModel> map = new HashMap<>();
        while (!list.isEmpty()) {
            String[] array = list.remove(0);
            String key = array[0].hashCode() + ":" + array[1].hashCode();
            PopularModel popular;
            if (map.containsKey(key))
                popular = map.get(key);
            else {
                popular = popularDao.find(array[0], array[1]);
                if (popular == null) {
                    popular = new PopularModel();
                    popular.setKey(array[0]);
                    popular.setValue(array[1]);
                }
                map.put(key, popular);
            }
            popular.setCount(popular.getCount() + 1);
        }
        map.values().forEach(popularDao::save);
    }
}
