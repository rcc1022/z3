package org.lpw.clivia.user;

import java.util.Map;

import org.lpw.clivia.push.PushScene;
import org.springframework.stereotype.Service;

@Service(UserModel.NAME + ".sms.scene")
public class PushSceneImpl implements PushScene {
    @Override
    public Map<String, String> scenes() {
        return Map.of(UserService.SMS_SIGN_IN, UserModel.NAME + ".sign-in", UserService.SMS_SIGN_UP,
                UserModel.NAME + ".sign-up", UserService.SMS_RESET_PASSWORD, UserModel.NAME + ".reset-password");
    }
}
