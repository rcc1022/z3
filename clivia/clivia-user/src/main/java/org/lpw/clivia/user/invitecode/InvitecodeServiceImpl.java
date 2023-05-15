package org.lpw.clivia.user.invitecode;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(InvitecodeModel.NAME + ".service")
public class InvitecodeServiceImpl implements InvitecodeService {
    @Inject
    private Generator generator;
    @Inject
    private Validator validator;
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private InvitecodeDao invitecodeDao;

    @Override
    public JSONObject query(String batch) {
        return invitecodeDao.query(batch, pagination.getPageSize(20), pagination.getPageNum()).toJson((invitecode, object) -> {
            if (invitecode.getUser() != null)
                object.put("user", userService.get(invitecode.getUser()));
        });
    }

    @Override
    public void generate(String batch, int length, int count) {
        for (int i = 0; i < count; i++) {
            InvitecodeModel invitecode = new InvitecodeModel();
            invitecode.setBatch(batch);
            for (int j = 0; j < 1024; j++) {
                invitecode.setCode(generator.number(length));
                if (invitecodeDao.find(invitecode.getCode()) == null)
                    break;
            }
            invitecodeDao.save(invitecode);
        }
    }

    @Override
    public void use(String user, String code) {
        if (validator.isEmpty(code))
            return;

        InvitecodeModel invitecode = invitecodeDao.find(code);
        if (invitecode == null || invitecode.getUser() != null)
            return;

        invitecode.setUser(user);
        invitecode.setTime(dateTime.now());
        invitecodeDao.save(invitecode);
    }

    @Override
    public void delete(String id) {
        invitecodeDao.delete(id);
    }
}
