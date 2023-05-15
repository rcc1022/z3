package org.lpw.clivia.olcs;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserModel;

public interface OlcsConfig {
    void fill(UserModel user, JSONObject object);

    int overdue();
}
