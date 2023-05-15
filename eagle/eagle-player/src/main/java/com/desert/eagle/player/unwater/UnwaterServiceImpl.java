package com.desert.eagle.player.unwater;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.player.PlayerService;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;

@Service(UnwaterModel.NAME + ".service")
public class UnwaterServiceImpl implements UnwaterService {
    @Inject
    private DateTime dateTime;
    @Inject
    private Pagination pagination;
    @Inject
    private PlayerService playerService;
    @Inject
    private UnwaterDao unwaterDao;

    @Override
    public JSONObject query(String time) {
        return unwaterDao.query(time, pagination.getPageSize(20), pagination.getPageNum())
                .toJson((unwater, object) -> object.put("player", playerService.get(unwater.getPlayer())));
    }

    @Override
    public void save(String player, int amount) {
        UnwaterModel unwater = new UnwaterModel();
        unwater.setPlayer(player);
        unwater.setAmount(amount);
        unwater.setTime(dateTime.now());
        unwater.setMemo(dateTime.toString(unwater.getTime(), "yyyyMMdd"));
        unwaterDao.save(unwater);
    }
}
