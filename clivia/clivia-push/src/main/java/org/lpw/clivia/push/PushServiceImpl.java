package org.lpw.clivia.push;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.lock.LockHelper;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.crypto.Sign;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.ctrl.template.Templates;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Message;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

@Service(PushModel.NAME + ".service")
public class PushServiceImpl implements PushService, ContextRefreshedListener, MinuteJob {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Message message;
    @Inject
    private Generator generator;
    @Inject
    private Numeric numeric;
    @Inject
    private Json json;
    @Inject
    private Sign sign;
    @Inject
    private Http http;
    @Inject
    private Logger logger;
    @Inject
    private Cache cache;
    @Inject
    private Pagination pagination;
    @Inject
    private Header header;
    @Inject
    private Templates templates;
    @Inject
    private Session session;
    @Inject
    private LockHelper lockHelper;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private PushDao pushDao;
    @Value("${" + PushModel.NAME + ".synch.url:}")
    private String synchUrl;
    @Value("${" + PushModel.NAME + ".synch.key:}")
    private String synchKey;
    private final Map<String, PushSender> senders = new HashMap<>();

    @Override
    public JSONObject query(String scene, String sender, String name, int state) {
        return pushDao.query(scene, sender, name, state, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray scenes() {
        JSONArray scenes = new JSONArray();
        BeanFactory.getBeans(PushScene.class).forEach(scene -> scene.scenes().forEach((key, value) -> {
            JSONObject object = new JSONObject();
            object.put("id", key);
            object.put("name", message.get(value));
            scenes.add(object);
        }));

        return scenes;
    }

    @Override
    public JSONArray senders() {
        JSONArray senders = new JSONArray();
        BeanFactory.getBeans(PushSender.class).forEach(sender -> {
            JSONObject object = new JSONObject();
            object.put("id", sender.key());
            object.put("name", message.get(sender.name()));
            senders.add(object);
        });

        return senders;
    }

    @Override
    public void save(PushModel push) {
        PushModel model = validator.isId(push.getId()) ? pushDao.findById(push.getId()) : null;
        if (model == null) {
            push.setId(null);
            push.setTime(dateTime.now());
        } else
            push.setTime(model.getTime());
        pushDao.save(push);
    }

    @Override
    public void state(String id, int state) {
        pushDao.state(id, state);
    }

    @Override
    public void delete(String id) {
        pushDao.delete(id);
    }

    @Override
    public Object send(String scene, JSONObject args) {
        PushModel push = pushDao.find(scene, 1);
        if (push == null) {
            logger.warn(null, "无可用的推送配置[{}:{}]！", scene, args);

            return templates.get().failure(108901, message.get(PushModel.NAME + ".null"), null, null);
        }

        push.setTime(dateTime.now());
        pushDao.save(push);

        PushSender sender = senders.get(push.getSender());
        if (sender == null) {
            logger.warn(null, "无可用的推送器[{}:{}:{}]！", scene, push.getSender(), args);

            return templates.get().failure(108902, message.get(PushModel.NAME + ".no-sender"), null, null);
        }

        JSONObject config = json.toObject(push.getConfig());
        Object object = sender.push(push, config, args);
        if (logger.isInfoEnable())
            logger.info("推送[{}:{}:{}:{}:{}]。", scene, push.getSender(), config, args, object);
        if (object == null)
            return templates.get().failure(108903, message.get(PushModel.NAME + ".push.fail"), null, null);

        return object;
    }

    @Override
    public Object captcha(String scene, String mobile) {
        String key = PushModel.NAME + ".captcha";
        String captcha = keyvalueService.value("setting.global.sms.captcha");
        if (!validator.isEmpty(captcha)) {
            session.set(key, captcha);

            return new JSONObject();
        }

        String prefix = key + ":" + TimeUnit.Minute.now() + ":";
        if (cache.get(prefix + mobile) != null || cache.get(prefix + header.getIp()) != null)
            return templates.get().failure(108911, message.get(PushModel.NAME + ".frequently"), null, null);

        String code = numeric.toString(generator.random(100000, 999999));
        session.set(key, code);
        cache.put(prefix + mobile, code, false);
        cache.put(prefix + header.getIp(), code, false);

        JSONObject args = new JSONObject();
        args.put("mobile", mobile);
        args.put("content", code);

        return send(scene, args);
    }

    @Override
    public boolean captcha(String code) {
        String c = session.remove(PushModel.NAME + ".captcha");

        return !validator.isEmpty(c) && c.equals(code);
    }

    @Override
    public int getContextRefreshedSort() {
        return 108;
    }

    @Override
    public void onContextRefreshed() {
        BeanFactory.getBeans(PushSender.class).forEach(sender -> senders.put(sender.key(), sender));
    }

    @Override
    public void executeMinuteJob() {
        if (validator.isEmpty(synchUrl) || Calendar.getInstance().get(Calendar.MINUTE) % 5 > 0)
            return;

        Map<String, String> parameter = new HashMap<>();
        sign.put(parameter, synchKey);
        String string = http.get(synchUrl + "/push/query", null, parameter);
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("data")) {
            logger.warn(null, "获取推送[{}:{}]同步数据[{}]失败！", synchUrl, parameter, string);

            return;
        }

        JSONArray list = object.getJSONObject("data").getJSONArray("list");
        if (validator.isEmpty(list))
            return;

        String lockId = lockHelper.lock(PushModel.NAME + ".synch", 5000, 60);
        if (lockId == null)
            return;

        for (int i = 0, size = list.size(); i < size; i++) {
            JSONObject obj = list.getJSONObject(i);
            String id = obj.getString("id");
            PushModel push = pushDao.findById(id);
            boolean isNull = push == null;
            if (isNull) {
                push = new PushModel();
                push.setId(id);
            }
            push.setScene(obj.getString("scene"));
            push.setSender(obj.getString("sender"));
            push.setName(obj.getString("name"));
            push.setConfig(obj.getString("config"));
            push.setCert(obj.getString("cert"));
            push.setState(obj.getIntValue("state"));
            push.setTime(dateTime.toTime(obj.getString("time")));
            if (isNull)
                pushDao.insert(push);
            else
                pushDao.save(push);
        }
        lockHelper.unlock(lockId);
    }
}
