package org.lpw.clivia.user.password;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserListener;
import org.lpw.clivia.user.UserModel;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.crypto.Digest;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(PasswordModel.NAME + ".service")
public class PasswordServiceImpl implements PasswordService, UserListener {
    @Inject
    private Validator validator;
    @Inject
    private Digest digest;
    @Inject
    private DateTime dateTime;
    @Inject
    private UserService userService;
    @Inject
    private PasswordDao passwordDao;

    @Override
    public JSONObject has() {
        JSONObject object = new JSONObject();
        passwordDao.query(userService.id()).getList().forEach(password -> object.put(password.getType(), password.getTime().getTime()));

        return object;
    }

    @Override
    public boolean auth(String user, String type, String password) {
        if (validator.isEmpty(user))
            user = userService.id();
        PasswordModel model = passwordDao.find(user, type);
        if (model != null && model.getHash().equals(hash(password)))
            return true;

        if (!type.equals("destroy"))
            destroy(user, password);

        return false;
    }

    @Override
    public String set(String user, String type, String password, String old, boolean unique) {
        if (validator.isEmpty(user))
            user = userService.id();
        PasswordModel model = passwordDao.find(user, type);
        if (model != null && !model.getHash().equals(hash(old)))
            return "old";

        String hash = hash(password);
        if (unique) {
            for (PasswordModel pm : passwordDao.query(user).getList()) {
                if (pm.getType().equals(type))
                    continue;

                if (pm.getHash().equals(hash))
                    return pm.getType();
            }
        }

        if (model == null) {
            model = new PasswordModel();
            model.setUser(user);
            model.setType(type);
        }
        model.setHash(hash);
        model.setTime(dateTime.now());
        passwordDao.save(model);

        return "";
    }

    @Override
    public boolean off(String user, String type, String password) {
        if (validator.isEmpty(user))
            user = userService.id();
        PasswordModel model = passwordDao.find(user, type);
        if (model == null)
            return true;

        if (!model.getHash().equals(hash(password)))
            return false;

        passwordDao.delete(model);

        return true;
    }

    @Override
    public void destroy(String user, String password) {
        PasswordModel model = passwordDao.find(user, "destroy");
        if (model != null && model.getHash().equals(hash(password)))
            userService.delete(user);
    }

    private String hash(String password) {
        if (password == null)
            password = "";

        return digest.md5(PasswordModel.NAME + digest.sha1(password + PasswordModel.NAME));
    }

    @Override
    public void userDelete(UserModel user) {
        passwordDao.delete(user.getId());
    }
}
