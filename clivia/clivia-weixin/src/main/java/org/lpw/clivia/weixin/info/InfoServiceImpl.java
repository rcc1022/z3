package org.lpw.clivia.weixin.info;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.lock.LockHelper;
import org.lpw.clivia.weixin.WeixinService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

@Service(InfoModel.NAME + ".service")
public class InfoServiceImpl implements InfoService, MinuteJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private LockHelper lockHelper;
    @Inject
    private WeixinService weixinService;
    @Inject
    private InfoDao infoDao;

    @Override
    public JSONArray query() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -10);

        return modelHelper.toJson(infoDao.query(new Timestamp(calendar.getTimeInMillis())).getList());
    }

    @Override
    public List<InfoModel> query(String unionId) {
        return infoDao.query(unionId).getList();
    }

    @Override
    public String findOpenId(String appId, String id) {
        InfoModel info = infoDao.find(id);
        if (info == null)
            return findOpenIdByUnionId(appId, id);

        if (info.getAppId().equals(appId))
            return id;

        return findOpenIdByUnionId(appId, info.getUnionId());
    }

    private String findOpenIdByUnionId(String appId, String unionId) {
        InfoModel info = infoDao.find(appId, unionId);

        return info == null ? null : info.getOpenId();
    }

    @Override
    public InfoModel find(String openId) {
        return infoDao.find(openId);
    }

    @Override
    public String save(String key, String appId, String unionId, String openId) {
        if (validator.isEmpty(openId))
            return null;

        InfoModel info = infoDao.find(openId);
        if (info == null)
            info = create(key, appId, unionId, openId);
        else if (validator.isEmpty(info.getUnionId()) && !validator.isEmpty(unionId)) {
            info.setUnionId(unionId);
            infoDao.save(info);
        }

        return info.getUnionId();
    }

    @Override
    public void executeMinuteJob() {
        JSONObject object = weixinService.sync("/weixin/info/query", new HashMap<>());
        if (object == null)
            return;

        JSONArray array = object.getJSONArray("data");
        if (array.isEmpty())
            return;

        String lockId = lockHelper.lock(InfoModel.NAME + ".minute", 100, 10);
        if (lockId == null)
            return;

        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject obj = array.getJSONObject(i);
            String openId = obj.getString("openId");
            if (validator.isEmpty(openId) || infoDao.find(openId) != null)
                continue;

            create(obj.getString("key"), obj.getString("appId"), obj.getString("unionId"), openId);
        }
        lockHelper.unlock(lockId);
    }

    private InfoModel create(String key, String appId, String unionId, String openId) {
        InfoModel info = new InfoModel();
        info.setKey(key);
        info.setAppId(appId);
        info.setUnionId(validator.isEmpty(unionId) ? "" : unionId);
        info.setOpenId(openId);
        info.setTime(dateTime.now());
        infoDao.save(info);

        return info;
    }
}
