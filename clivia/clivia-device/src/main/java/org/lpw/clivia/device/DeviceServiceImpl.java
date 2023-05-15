package org.lpw.clivia.device;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.cache.Cache;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(DeviceModel.NAME + ".service")
public class DeviceServiceImpl implements DeviceService, UserListener {
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Cache cache;
    @Inject
    private Session session;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private DeviceDao deviceDao;

    @Override
    public JSONObject query() {
        return deviceDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public DeviceModel find(String sid) {
        return cache.computeIfAbsent(DeviceModel.NAME + ":" + sid, key -> deviceDao.find(sid), false);
    }

    @Override
    public void save(String type, String identifier, String lang) {
        String sid = session.getId();
        DeviceModel device = deviceDao.find(sid);
        if (device == null) {
            device = new DeviceModel();
            device.setSid(sid);
        }
        device.setUser(userService.id());
        device.setType(type);
        device.setIdentifier(identifier);
        device.setLang(lang);
        device.setTime(dateTime.now());
        deviceDao.save(device);
        cleanCache(sid);
    }

    @Override
    public void userSignOut(UserModel user) {
        deviceDao.delete(session.getId());
        cleanCache(session.getId());
    }

    @Override
    public void userDelete(UserModel user) {
        List<DeviceModel> list = deviceDao.query(user.getId()).getList();
        if (list.isEmpty())
            return;

        deviceDao.deletes(user.getId());
        list.forEach(device -> cleanCache(device.getSid()));
    }

    private void cleanCache(String sid) {
        cache.remove(DeviceModel.NAME + ":" + sid);
    }
}
