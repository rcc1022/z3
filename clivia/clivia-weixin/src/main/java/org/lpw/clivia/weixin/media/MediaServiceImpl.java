package org.lpw.clivia.weixin.media;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.temporary.Temporary;
import org.lpw.clivia.weixin.WeixinModel;
import org.lpw.clivia.weixin.WeixinService;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Http;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Logger;
import org.lpw.photon.wormhole.WormholeHelper;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Service(MediaModel.NAME + ".service")
public class MediaServiceImpl implements MediaService {
    @Inject
    private DateTime dateTime;
    @Inject
    private Http http;
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Logger logger;
    @Inject
    private WormholeHelper wormholeHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private Temporary temporary;
    @Inject
    private WeixinService weixinService;
    @Inject
    private MediaDao mediaDao;

    @Override
    public JSONObject query(String key, String appId, String type, String name, String time) {
        return mediaDao.query(key, appId, type, name, time, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void create(MediaModel media) {
        String uri = media.getUri();
        String file = context.getAbsolutePath(temporary.newSavePath(uri.substring(uri.lastIndexOf('.'))));
        wormholeHelper.download(uri, file);
        Map<String, File> files = new HashMap<>();
        files.put("media", new File(file));

        WeixinModel weixin = weixinService.findByKey(media.getKey());
        JSONObject object = weixinService.byAccessToken(weixin, accessToken -> {
            String string = http.upload("https://api.weixin.qq.com/cgi-bin/material/add_material?access_token=" + accessToken
                    + "&type=" + media.getType(), null, null, files);
            if (logger.isDebugEnable())
                logger.debug("上传微信媒体文件[{}]。", string);

            return string;
        });
        io.delete(file);
        if (object == null || !object.containsKey("media_id"))
            return;

        media.setId(null);
        media.setAppId(weixin.getAppId());
        media.setMediaId(object.getString("media_id"));
        media.setUrl(object.getString("url"));
        media.setTime(dateTime.now());
        mediaDao.save(media);
    }

    @Override
    public void delete(String id) {
        MediaModel media = mediaDao.findById(id);
        JSONObject object = new JSONObject();
        object.put("media_id", media.getMediaId());
        weixinService.byAccessToken(weixinService.findByAppId(media.getAppId()), accessToken -> http.post(
                "https://api.weixin.qq.com/cgi-bin/material/del_material?access_token=" + accessToken,
                null, object.toJSONString()));
        mediaDao.delete(id);
    }
}
