package com.desert.eagle.home;

import org.lpw.clivia.Permit;
import org.lpw.photon.ctrl.Forward;
import org.lpw.photon.ctrl.context.Request;
import org.lpw.photon.ctrl.execute.Execute;
import org.springframework.stereotype.Controller;

import javax.inject.Inject;

@Controller(HomeModel.NAME + ".ctrl")
@Execute(name = "/home/", key = HomeModel.NAME, code = "251")
public class HomeCtrl {
    @Inject
    private Request request;
    @Inject
    private Forward forward;
    @Inject
    private HomeService homeService;

    @Execute(name = "index", permit = Permit.always)
    public Object index() {
        forward.redirectTo(homeService.index(request.get("invite"), false));

        return "";
    }

    @Execute(name = "index1", permit = Permit.always)
    public Object index1() {
        forward.redirectTo(homeService.index(request.get("invite"), true));

        return "";
    }

    @Execute(name = "wx-sign-in-url", permit = Permit.always)
    public Object wxSignInUrl() {
        return homeService.wxSignInUrl(request.get("host"), request.get("fingerprint"));
    }

    @Execute(name = "wx-sign-in", permit = Permit.always)
    public Object wxSignIn() {
        String host = request.get("host");
        String signIn = homeService.wxSignIn(request.get("code"), host);
        forward.redirectTo(signIn == null ? ("http://" + host) : signIn);

        return null;
    }

    @Execute(name = "2auth", permit = Permit.always)
    public Object toAuth() {
        return homeService.toAuth(request.get("fingerprint"));
    }

    @Execute(name = "auth", permit = Permit.always)
    public Object auth() {
//        homeService.auth(request.get("code"));
        forward.redirectTo(homeService.auth(request.get("code")));

        return null;
    }
}