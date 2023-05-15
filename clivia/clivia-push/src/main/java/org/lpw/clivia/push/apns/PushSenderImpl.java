package org.lpw.clivia.push.apns;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.ApnsPushNotification;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.TokenUtil;
import org.lpw.clivia.push.PushModel;
import org.lpw.clivia.push.PushSender;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.File;
import java.util.Optional;

@Service("clivia.push.apns.sender")
public class PushSenderImpl implements PushSender {
    @Inject
    private Json json;
    @Inject
    private Context context;
    @Inject
    private Logger logger;

    @Override
    public String key() {
        return "apns";
    }

    @Override
    public String name() {
        return "clivia.push.apns.name";
    }

    @Override
    public Object push(PushModel push, JSONObject config, JSONObject args) {
        String cert = cert(push.getCert());
        if (cert == null)
            return null;

        try {
            ApnsClient client = new ApnsClientBuilder().setApnsServer(ApnsClientBuilder.PRODUCTION_APNS_HOST)
                    .setClientCredentials(new File(context.getAbsolutePath(cert)), config.getString("password")).build();
            ApnsPushNotification notification = new SimpleApnsPushNotification(TokenUtil.sanitizeTokenString(args.getString("token")),
                    config.getString("appid"), new SimpleApnsPayloadBuilder().setAlertTitle(args.getString("title"))
                    .setAlertBody(args.getString("body")).build());
            String result = result(client.sendNotification(notification).get());
            client.close();

            return result;
        } catch (Throwable throwable) {
            logger.warn(throwable, "推送APNs信息[{}:{}:{}]时发生异常！", push.getId(), config, args);

            return null;
        }
    }

    private String cert(String cert) {
        JSONArray array = json.toArray(cert);
        if (array == null || array.isEmpty())
            return null;

        return array.getJSONObject(0).getString("path");
    }

    private String result(PushNotificationResponse<ApnsPushNotification> response) {
        if (response.isAccepted())
            return "success";

        Optional<String> reason = response.getRejectionReason();

        return reason.isEmpty() ? null : reason.get();
    }
}
