package org.lpw.clivia.upgrader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;

@Service(UpgraderModel.NAME + ".service")
public class UpgraderServiceImpl implements UpgraderService, MinuteJob {
    @Inject
    private Validator validator;
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Request request;
    @Inject
    private Pagination pagination;
    @Inject
    private UpgraderDao upgraderDao;
    @Value("${" + UpgraderModel.NAME + ".synch:}")
    private String synch;

    @Override
    public JSONObject query() {
        return upgraderDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject latest(String client) {
        if (validator.isEmpty(client)) {
            UpgraderModel upgrader = upgraderDao.latest();
            if (upgrader == null)
                return new JSONObject();

            JSONObject object = modelHelper.toJson(upgrader);
            object.put("explain", json.toObject(upgrader.getExplain()));

            return object;
        }

        JSONObject object = new JSONObject();
        for (UpgraderModel upgrader : upgraderDao.query(0, 0).getList()) {
            String url = switch (client) {
                case "android", "0" -> upgrader.getAndroid();
                case "ios", "1" -> upgrader.getIos();
                case "windows", "2" -> upgrader.getWindows();
                case "macos", "3" -> upgrader.getMacos();
                case "linux", "4" -> upgrader.getLinux();
                default -> "";
            };
            if (validator.isEmpty(url))
                continue;

            object.put("version", upgrader.getVersion());
            object.put("name", upgrader.getName());
            object.put("explain", json.toObject(upgrader.getExplain()));
            object.put("url", url);
            break;
        }

        return object;
    }

    @Override
    public JSONObject plist() {
        JSONObject object = latest("ios");
        if (object.containsKey("url"))
            object.put("url", request.getUrl().replaceAll("http://", "https://") + object.getString("url"));

        return object;
    }

    @Override
    public void save(UpgraderModel upgrader) {
        if (validator.isEmpty(upgrader.getId()) || upgraderDao.findById(upgrader.getId()) == null)
            upgrader.setId(null);
        JSONArray array = json.toArray(upgrader.getFile());
        if (!validator.isEmpty(array)) {
            int size = array.size();
            for (int i = 0; i < size; i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                if (validator.isEmpty(upgrader.getAndroid()) && (name.contains("android") || name.endsWith(".apk")))
                    upgrader.setAndroid(object.getString("uri"));
                if (validator.isEmpty(upgrader.getIos()) && (name.contains("ios") || name.endsWith(".ipa")))
                    upgrader.setIos(object.getString("uri"));
                if (validator.isEmpty(upgrader.getWindows()) && (name.contains("windows") || name.endsWith(".exe")))
                    upgrader.setWindows(object.getString("uri"));
                if (validator.isEmpty(upgrader.getMacos()) && name.contains("macos"))
                    upgrader.setMacos(object.getString("uri"));
                if (validator.isEmpty(upgrader.getLinux()) && name.contains("linux"))
                    upgrader.setLinux(object.getString("uri"));
            }
        }
        upgraderDao.save(upgrader);
    }

    @Override
    public void delete(String id) {
        upgraderDao.delete(id);
    }

    @Override
    public void executeMinuteJob() {
        if (validator.isEmpty(synch) || Calendar.getInstance().get(Calendar.MINUTE) % 5 > 0)
            return;

        String string = http.get(synch + "/upgrader/latest", null, "");
        JSONObject object = json.toObject(string);
        if (object == null || !object.containsKey("data")) {
            logger.warn(null, "获取更新配置[{}]信息[{}]失败！", synch, string);

            return;
        }

        UpgraderModel upgrader = modelHelper.fromJson(object.getJSONObject("data"), UpgraderModel.class);
        if (upgrader == null) {
            logger.warn(null, "解析更新配置[{}]信息[{}]失败！", synch, string);

            return;
        }

        if (upgraderDao.findById(upgrader.getId()) == null)
            upgraderDao.insert(upgrader);
        else
            upgraderDao.save(upgrader);

        JSONArray array = json.toArray(upgrader.getFile());
        if (validator.isEmpty(array))
            return;

        for (int i = 0, size = array.size(); i < size; i++) {
            JSONObject file = array.getJSONObject(i);
            if (!file.containsKey("uri"))
                continue;

            String uri = file.getString("uri");
            String path = context.getAbsolutePath(uri);
            if (io.exists(path))
                continue;

            io.mkdirs(path.substring(0, path.lastIndexOf('/')));
            try (OutputStream outputStream = new FileOutputStream(path)) {
                http.get(synch + uri, null, null, null, outputStream);
            } catch (Throwable throwable) {
                logger.warn(throwable, "下载更新文件[{}:{}]时发生异常！", synch, uri);
            }
        }
    }
}
