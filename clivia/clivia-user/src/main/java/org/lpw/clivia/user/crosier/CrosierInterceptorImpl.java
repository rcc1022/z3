package org.lpw.clivia.user.crosier;

import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.online.OnlineService;
import org.lpw.photon.crypto.Sign;
import org.lpw.photon.ctrl.Failure;
import org.lpw.photon.ctrl.Interceptor;
import org.lpw.photon.ctrl.Invocation;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.security.TrustfulIp;
import org.lpw.photon.ctrl.validate.SignValidator;
import org.lpw.photon.util.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(CrosierModel.NAME + ".interceptor")
public class CrosierInterceptorImpl implements Interceptor {
    @Inject
    private Sign sign;
    @Inject
    private Logger logger;
    @Inject
    private Header header;
    @Inject
    private Request request;
    @Inject
    private TrustfulIp trustfulIp;
    @Inject
    private SignValidator signValidator;
    @Inject
    private UserService userService;
    @Inject
    private CrosierService crosierService;
    @Inject
    private OnlineService onlineService;
    @Value("${" + CrosierModel.NAME + ".on:false}")
    private boolean on;

    @Override
    public int getSort() {
        return 151;
    }

    @Override
    public Object execute(Invocation invocation) throws Exception {
        onlineService.visit();
        if (!on)
            return invocation.invoke();

        if (trustfulIp.contains(header.getIp()) || sign.verify(request.getMap(), null) || crosierService.permit(request.getUri(), request.getMap())) {
            signValidator.setSignEnable(false);

            return invocation.invoke();
        }

        logger.warn(null, "未授权用户[{}:{}]访问[{}:{}:{}]。", header.getIp(), userService.sign(), request.getUri(), header.getMap(), request.getMap());

        return Failure.NotPermit;
    }
}
