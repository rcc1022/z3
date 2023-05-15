package org.lpw.clivia.push.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.push.model.v20160801.PushRequest;
import com.google.gson.Gson;
import org.lpw.clivia.push.PushModel;
import org.lpw.clivia.push.PushSender;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Numeric;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("clivia.push.aliyun.sender")
public class PushSenderImpl implements PushSender {
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;

    @Override
    public String key() {
        return "aliyun";
    }

    @Override
    public String name() {
        return "clivia.push.aliyun.name";
    }

    @Override
    public Object push(PushModel push, JSONObject config, JSONObject args) {
        if (!config.containsKey("accessKeyId") || !config.containsKey("accessSecret") || !config.containsKey("appKey")
                || !args.containsKey("device") || !args.containsKey("title") || !args.containsKey("body")) {
            logger.warn(null, "阿里云推送配置[{}]参数[{}]错误！", config, args);

            return null;
        }

        PushRequest request = new PushRequest();
        request.setAppKey(numeric.toLong(config.getString("appKey")));
        request.setPushType("NOTICE");
        request.setDeviceType("ALL");
        request.setTarget("DEVICE");
        request.setTargetValue(args.getString("device"));
        request.setAndroidNotificationChannel(config.getString("channel"));
        request.setTitle(args.getString("title"));
        request.setBody(args.getString("body"));
        if (args.containsKey("badge"))
            request.setIOSBadge(args.getInteger("badge"));
        if (args.containsKey("iOSMusic"))
            request.setIOSMusic(args.getString("iOSMusic"));

        try {
            return new Gson().toJson(new DefaultAcsClient(DefaultProfile.getProfile("cn-hangzhou",
                    config.getString("accessKeyId"), config.getString("accessSecret"))).getAcsResponse(request));
        } catch (Throwable throwable) {
            logger.warn(throwable, "阿里云推送[{}:{}]异常！", config, args);

            return null;
        }
    }
}
