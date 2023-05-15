package org.lpw.clivia.user.invitecode;

import com.alibaba.fastjson.JSONObject;

public interface InvitecodeService {
    String VALIDATOR_VALID = InvitecodeModel.NAME + ".validator.valid";

    JSONObject query(String batch);

    void generate(String batch, int length, int count);

    void use(String user, String code);

    void delete(String id);
}
