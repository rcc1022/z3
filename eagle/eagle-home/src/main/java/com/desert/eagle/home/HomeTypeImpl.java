package com.desert.eagle.home;

import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.type.TypeSupport;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller(HomeModel.NAME + ".type")
public class HomeTypeImpl extends TypeSupport implements HomeType {
    private final ThreadLocal<String[]> threadLocal = new ThreadLocal<>();

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public UserModel auth(String s, String s1, String s2) {
        return null;
    }

    @Override
    public Set<String> getUid(String s, String s1) {
        return null;
    }

    @Override
    public void set(String openId, String unionId, String nick, String avatar) {
        threadLocal.set(new String[]{openId, unionId, nick, avatar});
    }
}
