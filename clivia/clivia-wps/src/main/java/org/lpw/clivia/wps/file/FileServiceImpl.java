package org.lpw.clivia.wps.file;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.wps.WpsModel;
import org.lpw.clivia.wps.WpsService;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.ctrl.http.ServiceHelper;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Converter;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(FileModel.NAME + ".service")
public class FileServiceImpl implements FileService, ContextRefreshedListener {
    private static final String USER = "_w_user";
    private static final String SIGNATURE = "_w_signature";

    @Inject
    private Context context;
    @Inject
    private Converter converter;
    @Inject
    private Digest digest;
    @Inject
    private Codec codec;
    @Inject
    private Validator validator;
    @Inject
    private Json json;
    @Inject
    private Logger logger;
    @Inject
    private ServiceHelper serviceHelper;
    @Inject
    private UserService userService;
    @Inject
    private WpsService wpsService;
    @Inject
    private FileDao fileDao;
    private final Map<String, String> types = new HashMap<>();

    @Override
    public String preview(String key, String uri, String name, Permission permission, String creator, long create) {
        int index = uri.lastIndexOf('.');
        if (index == -1)
            return null;

        String suffix = uri.substring(index + 1);
        if (!types.containsKey(suffix))
            return null;

        File file = new File(context.getAbsolutePath(uri));
        if (!file.exists())
            return null;

        WpsModel wps = wpsService.findByKey(key);
        if (wps == null)
            return null;

        FileModel model = fileDao.find(wps.getId(), uri, permission.ordinal());
        if (model == null) {
            model = new FileModel();
            model.setWps(wps.getId());
            model.setUri(uri);
            model.setName(name);
            model.setPermission(permission.ordinal());
            model.setVersion(1);
            model.setSize(file.length());
            model.setCreator(creator);
            model.setCreateTime(create);
            model.setModifier(creator);
            model.setModifyTime(create);
            fileDao.save(model);
        }

        Map<String, String> map = new HashMap<>();
        map.put("_w_appid", wps.getAppId());
        map.put(USER, userService.id());
        map.put(SIGNATURE, signature(wps, map));
        StringBuilder sb = new StringBuilder("https://wwo.wps.cn/office/").append(types.get(suffix)).append('/').append(model.getId()).append('?');
        map.forEach((k, v) -> sb.append(k).append('=').append(codec.encodeUrl(v, null)).append('&'));

        return sb.substring(0, sb.length() - 1);
    }

    @Override
    public JSONObject info(String id, Map<String, String> map) {
        FileModel file = fileDao.findById(id);
        if (file == null)
            return failure(1, "id " + id + " not exists");

        WpsModel wps = wpsService.findById(file.getWps());
        if (wps == null)
            return failure(2, "wps " + id + " not exists");

        JSONObject validate = validate(wps, map);
        if (validate != null)
            return validate;

        JSONObject object = new JSONObject();
        JSONObject f = new JSONObject();
        f.put("id", id);
        f.put("name", file.getName());
        f.put("version", file.getVersion());
        f.put("size", file.getSize());
        f.put("creator", file.getCreator());
        f.put("create_time", file.getCreateTime());
        f.put("modifier", file.getModifier());
        f.put("modify_time", file.getModifyTime());
        f.put("download_url", serviceHelper.getUrl() + file.getUri());
        f.put("preview_pages", 99);
        JSONObject acl = new JSONObject();
        acl.put("rename", file.getPermission());
        acl.put("history", file.getPermission());
        acl.put("copy", file.getPermission());
        acl.put("export", file.getPermission());
        acl.put("print", file.getPermission());
        f.put("user_acl", acl);
        JSONObject watermark = new JSONObject();
        f.put("watermark", watermark);
        if (validator.isEmpty(wps.getWatermark()))
            watermark.put("type", 0);
        else {
            watermark.put("type", 1);
            watermark.put("value", wps.getWatermark());
        }
        object.put("file", f);
        JSONObject u = user(map.get(USER), wps);
        u.put("permission", file.getPermission() == 1 ? "write" : "read");
        object.put("user", u);

        return object;
    }

    @Override
    public JSONObject user(String id, Map<String, String> map, String body) {
        FileModel file = fileDao.findById(id);
        if (file == null)
            return failure(1, "id " + id + " not exists");

        WpsModel wps = wpsService.findById(file.getWps());
        if (wps == null)
            return failure(2, "wps " + id + " not exists");

        JSONObject validate = validate(wps, map);
        if (validate != null)
            return validate;

        JSONObject b = json.toObject(body);
        if (!json.containsKey(b, "ids"))
            return failure(4, "illegal body " + body);

        JSONArray ids = b.getJSONArray("ids");
        JSONArray users = new JSONArray();
        for (int i = 0, size = ids.size(); i < size; i++)
            users.add(user(ids.getString(i), wps));
        JSONObject object = new JSONObject();
        object.put("users", users);

        return object;
    }

    private JSONObject user(String id, WpsModel wps) {
        JSONObject u = new JSONObject();
        UserModel user = userService.findById(id);
        if (user == null) {
            u.put("id", "");
            u.put("name", wps.getNick());
        } else {
            u.put("id", user.getId());
            u.put("name", user.getNick());
        }
        u.put("avatar_url", avatar(user, wps));

        return u;
    }

    private String avatar(UserModel user, WpsModel wps) {
        if (user != null)
            if (!validator.isEmpty(user.getAvatar()))
                return user.getAvatar().contains("://") ? user.getAvatar() : (serviceHelper.getUrl() + user.getAvatar());

        return validator.isEmpty(wps.getAvatar()) ? "" : (serviceHelper.getUrl() + wps.getAvatar());
    }

    private JSONObject validate(WpsModel wps, Map<String, String> map) {
        return signature(wps, map).equals(map.get(SIGNATURE)) ? null : failure(3, "illegal signature");
    }

    private String signature(WpsModel wps, Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        List<String> keys = new ArrayList<>(map.keySet());
        keys.remove(SIGNATURE);
        Collections.sort(keys);
        keys.stream().filter(key -> key.startsWith("_w_")).forEach(key -> sb.append(key).append('=').append(map.get(key)));
        sb.append("_w_secretkey=").append(wps.getSecret());

        return codec.encodeBase64(digest.hmacSHA1(wps.getSecret().getBytes(), sb.toString().getBytes()));
    }

    private JSONObject failure(int code, String msg) {
        JSONObject object = new JSONObject();
        object.put("code", 157100 + code);
        object.put("msg", msg);

        return object;
    }

    @Override
    public void delete(String wps) {
        fileDao.delete(wps);
    }

    @Override
    public int getContextRefreshedSort() {
        return 157;
    }

    @Override
    public void onContextRefreshed() {
        for (String key : converter.toArray("doc,dot,wps,wpt,docx,dotx,docm,dotm,rtf", ",")) {
            types.put(key, "w");
        }
        for (String key : converter.toArray("xls,xlt,et,xlsx,xltx,csv,xlsm,xltm", ",")) {
            types.put(key, "s");
        }
        for (String key : converter.toArray("ppt,pptx,pptm,ppsx,ppsm,pps,potx,potm,dpt,dps", ",")) {
            types.put(key, "p");
        }
        types.put("pdf", "f");
        logger.info("设置WPS后缀格式：{}。", types);
    }
}
