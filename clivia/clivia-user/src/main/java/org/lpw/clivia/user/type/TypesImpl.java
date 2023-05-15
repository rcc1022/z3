package org.lpw.clivia.user.type;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserModel;
import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

@Service("clivia.user.types")
public class TypesImpl implements Types, ContextRefreshedListener {
    @Inject
    private Logger logger;
    private Map<String, Type> map;

    @Override
    public boolean hasKey(String key) {
        return map.containsKey(key);
    }

    @Override
    public UserModel auth(String key, String uid, String password, String grade) {
        return map.get(key).auth(uid, password, grade);
    }

    @Override
    public Set<String> getUid(String key, String uid, String password) {
        return map.get(key).getUid(uid, password);
    }

    @Override
    public String getMobile(String key, String uid, String password) {
        return map.get(key).getMobile(uid, password);
    }

    @Override
    public String getEmail(String key, String uid, String password) {
        return map.get(key).getEmail(uid, password);
    }

    @Override
    public String getNick(String key, String uid, String password) {
        return map.get(key).getNick(uid, password);
    }

    @Override
    public String getAvatar(String key, String uid, String password) {
        return map.get(key).getAvatar(uid, password);
    }

    @Override
    public String getFrom(String key, String uid, String password) {
        return map.get(key).getFrom(uid, password);
    }

    @Override
    public JSONObject getAuth(String key, String uid, String password) {
        return map.get(key).getAuth(uid, password);
    }

    @Override
    public int getContextRefreshedSort() {
        return 151;
    }

    @Override
    public void onContextRefreshed() {
        map = new HashMap<>();
        BeanFactory.getBeans(Type.class).forEach(type -> map.put(type.getKey(), type));
        if (logger.isInfoEnable())
            logger.info("加载用户认证Type[{}]。", map);
    }
}
