package com.desert.eagle.robot;

import com.alibaba.fastjson.JSONObject;
import com.desert.eagle.bet.BetService;
import com.desert.eagle.game.GameModel;
import com.desert.eagle.game.GameService;
import com.desert.eagle.player.Robot;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(RobotModel.NAME + ".service")
public class RobotServiceImpl implements RobotService, Robot, SecondsJob, HourJob {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Pagination pagination;
    @Inject
    private GameService gameService;
    @Inject
    private BetService betService;
    @Inject
    private RobotDao robotDao;
    private Map<String, List<RobotModel>> map = new HashMap<>();

    @Override
    public JSONObject query() {
        return robotDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(RobotModel robot) {
        RobotModel model = validator.isId(robot.getId()) ? robotDao.findById(robot.getId()) : null;
        if (model == null)
            robot.setId(null);
        else
            robot.setGame(model.getGame());
        if (robot.getGame() == null)
            robot.setGame("");
        robotDao.save(robot);
    }

    @Override
    public void allot() {
        List<GameModel> games = gameService.list();
        List<RobotModel> list = robotDao.query(0, 0).getList();
        while (!games.isEmpty() && !list.isEmpty()) {
            int g = generator.random(0, games.size() - 1);
            GameModel game = games.get(g);
            if (game.getRobot() <= 0 || game.getType() > 11) {
                games.remove(g);

                continue;
            }

            RobotModel robot = list.remove(generator.random(0, list.size() - 1));
            robot.setGame(game.getId());
            robotDao.save(robot);

            game.setRobot(game.getRobot() - 1);
            if (game.getRobot() <= 0)
                games.remove(g);
        }
        list.forEach(robot -> {
            robot.setGame("");
            robotDao.save(robot);
        });
        load();
    }

    @Override
    public void delete(String id) {
        robotDao.delete(id);
    }

    @Override
    public String[] getNickAvatar(String id) {
        RobotModel robot = robotDao.findById(id);
        if (robot == null)
            return null;

        return new String[]{robot.getNick(), robot.getAvatar()};
    }

    @Override
    public void executeSecondsJob() {
        if (map.isEmpty()) {
            load();
        }
        if (map.isEmpty())
            return;

        map.forEach((game, list) -> {
            if (generator.random(0, 9) > 0 || list.isEmpty())
                return;

            RobotModel robot = list.get(generator.random(0, list.size() - 1));
            betService.robot(game, robot.getId());
        });
    }

    private void load() {
        Map<String, List<RobotModel>> map = new HashMap<>();
        robotDao.query(0, 0).getList().forEach(robot -> {
            if (!validator.isId(robot.getGame()))
                return;

            map.computeIfAbsent(robot.getGame(), key -> new ArrayList<>()).add(robot);
        });
        this.map = map;
    }

    @Override
    public void executeHourJob() {
        allot();
    }
}
