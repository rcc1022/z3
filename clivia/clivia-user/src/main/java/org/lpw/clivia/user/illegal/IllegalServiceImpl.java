package org.lpw.clivia.user.illegal;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.util.DateTime;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(IllegalModel.NAME + ".service")
public class IllegalServiceImpl implements IllegalService {
    @Inject
    private DateTime dateTime;
    @Inject
    private Header header;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private IllegalDao illegalDao;

    @Override
    public JSONObject query() {
        return illegalDao.query(pagination.getPageSize(20), pagination.getPageNum())
                .toJson((illegal, object) -> object.put("user", userService.get(illegal.getUser())));
    }

    @Override
    public void save(String user) {
        IllegalModel illegal = new IllegalModel();
        illegal.setUser(user);
        illegal.setIp(header.getIp());
        illegal.setTime(dateTime.now());
        illegalDao.save(illegal);
    }
}
