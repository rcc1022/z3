package com.desert.eagle.home;

import com.desert.eagle.domain.DomainModel;
import com.desert.eagle.domain.DomainService;
import org.lpw.clivia.keyvalue.KeyvalueListener;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.auth.AuthService;
import org.lpw.clivia.weixin.WeixinModel;
import org.lpw.clivia.weixin.WeixinService;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.util.Codec;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;

@Service(HomeModel.NAME + ".service")
public class HomeServiceImpl implements HomeService, KeyvalueListener {
    @Inject
    private Validator validator;
    @Inject
    private Codec codec;
    @Inject
    private Logger logger;
    @Inject
    private Header header;
    @Inject
    private Session session;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private UserService userService;
    @Inject
    private AuthService authService;
    @Inject
    private WeixinService weixinService;
    @Inject
    private DomainService domainService;
    @Inject
    HomeType homeType;
    @Inject
    private HomeDao homeDao;

    @Override
    public String index(String invite, boolean need) {
        if (need && validator.isEmpty(invite))
            return keyvalueService.value("setting.home.illegal");

        StringBuilder sb = new StringBuilder("http://");
        DomainModel domain = domainService.find(2, 1);
        sb.append(domain == null ? header.get("host") : domain.getName());
        if (!validator.isEmpty(invite))
            sb.append("/?invite=").append(invite);

        return sb.toString();
    }

    @Override
    public String wxSignInUrl(String host, String fingerprint) {
        return wxSignInUrl("snsapi_base", host, fingerprint);
    }

    @Override
    public String wxSignIn(String code, String host) {
        userService.signIn(code, WeixinService.SINGLE_KEY, "weixin", "0");
        UserModel user = userService.fromSession();
        if (user == null || validator.isEmpty(user.getNick()))
            return wxSignInUrl("snsapi_userinfo", host);

        return null;
    }

    private String wxSignInUrl(String scope, String host, String fingerprint) {
        String url = weixinService.code(WeixinService.SINGLE_KEY,
                "http://" + keyvalueService.value("setting.weixin.domain") + "/home/wx-sign-in?photon-session-id=" + session.getId()
                        + "&host=" + host + "&fingerprint=" + fingerprint, scope);
        if (logger.isInfoEnable())
            logger.info("wx-sign-in-url:{}", url);

        return url;
    }

    @Override
    public String toAuth(String fingerprint) {
        DomainModel auth = domainService.find(1, 1);
        if (auth == null)
            return "";

        DomainModel to = domainService.find(2, 1);
        if (to == null)
            return "";

        WeixinModel weixin = weixinService.findByKey(WeixinService.SINGLE_KEY);
        if (weixin == null)
            return "";

        String url = "http://" + auth.getName() + "/?to=" + codec.encodeUrl("http://" + to.getName() + "/home/auth", null)
                + "&sid=" + session.getId() + "&fingerprint=" + fingerprint + "&appid=" + weixin.getAppId();
        if (logger.isInfoEnable())
            logger.info("to-auth-url:{}", url);

        return url;
    }

    @Override
    public String auth(String code) {
        userService.signIn(code, WeixinService.SINGLE_KEY, "weixin", "0");
        String url = "http://" + domainService.find(2, 1).getName() + "/";
        if (logger.isInfoEnable())
            logger.info("auth-url:{}", url);

        return url;
    }

    @Override
    public void keyvalueModify(Map<String, String> map) {
        if (!map.containsKey("setting.weixin.appid"))
            return;

        WeixinModel weixin = new WeixinModel();
        weixin.setAppId(map.get("setting.weixin.appid"));
        weixin.setSecret(map.get("setting.weixin.secret"));
        weixinService.singleSave(weixin);
    }

    @Override
    public void keyvalueDelete(Map<String, String> map) {

    }
}
