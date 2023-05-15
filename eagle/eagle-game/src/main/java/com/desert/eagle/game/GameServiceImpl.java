package com.desert.eagle.game;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Service(GameModel.NAME + ".service")
public class GameServiceImpl implements GameService, HourJob {
    @Inject
    private Validator validator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private Set<OverdueListener> listeners;
    @Inject
    private GameDao gameDao;

    @Override
    public JSONObject query(int on) {
        return gameDao.query(on, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public List<GameModel> list() {
        return gameDao.query(-1, 0, 0).getList();
    }

    @Override
    public GameModel get(String id) {
        return gameDao.findById(id);
    }

    @Override
    public GameModel find(int type) {
        return gameDao.findByType(type);
    }

    @Override
    public JSONObject json(String id) {
        GameModel game = gameDao.findById(id);

        return game == null ? new JSONObject() : modelHelper.toJson(game);
    }

    @Override
    public void save(GameModel game) {
        GameModel model = validator.isId(game.getId()) ? gameDao.findById(game.getId()) : null;
        if (model == null)
            game.setId(null);
        gameDao.save(game);
    }

    @Override
    public void on(String id, int on) {
        gameDao.on(id, on);
    }

    @Override
    public void delete(String id) {
        gameDao.delete(id);
    }

    @Override
    public String rule(String id) {
        GameModel game = gameDao.findById(id);

        return game == null || validator.isEmpty(game.getRule()) ? "" : game.getRule();
    }

    @Override
    public void executeHourJob() {
        int overdue = keyvalueService.valueAsInt("setting.data.overdue", 7);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -overdue);
        Timestamp time = new Timestamp(calendar.getTimeInMillis());
        listeners.forEach(listener -> listener.overdue(time));
        gameDao.close();
    }
}
