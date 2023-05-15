package org.lpw.clivia.user.stat;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.online.OnlineService;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.scheduler.MinuteJob;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Message;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.util.Calendar;

@Service(StatModel.NAME + ".service")
public class StatServiceImpl implements StatService, MinuteJob {
    @Inject
    private DateTime dateTime;
    @Inject
    private Message message;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private UserService userService;
    @Inject
    private OnlineService onlineService;
    @Inject
    private StatDao statDao;

    @Override
    public JSONObject query(String date) {
        return statDao.query(date, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject today() {
        StatModel stat = statDao.find(dateTime.today());
        if (stat == null)
            stat = new StatModel();

        return modelHelper.toJson(stat);
    }

    @Override
    public JSONArray week() {
        JSONArray array = new JSONArray();
        String[] series = new String[]{message.get(StatModel.NAME + ".count"),
                message.get(StatModel.NAME + ".register"), message.get(StatModel.NAME + ".online")};
        statDao.query(null, 7, 1).getList().forEach(stat -> {
            String x = dateTime.toString(stat.getDate(), "MMdd");
            week(array, series[0], x, stat.getCount());
            week(array, series[1], x, stat.getRegister());
            week(array, series[2], x, stat.getOnline());
        });

        return array;
    }

    private void week(JSONArray array, String series, String x, int y) {
        JSONObject object = new JSONObject();
        object.put("series", series);
        object.put("x", x);
        object.put("y", y);
        array.add(object);
    }

    @Override
    public JSONArray pie() {
        StatModel stat = statDao.find(dateTime.today());
        if (stat == null)
            stat = new StatModel();

        JSONArray array = new JSONArray();
        pie(array, message.get(StatModel.NAME + ".count"), stat.getCount());
        pie(array, message.get(StatModel.NAME + ".register"), stat.getRegister());
        pie(array, message.get(StatModel.NAME + ".online"), stat.getOnline());

        return array;
    }

    private void pie(JSONArray array, String name, int value) {
        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("value", value);
        array.add(object);
    }

    @Override
    public void executeMinuteJob() {
        Calendar calendar = Calendar.getInstance();
        count(new Date(calendar.getTimeInMillis()));
        if (calendar.get(Calendar.HOUR_OF_DAY) == 0) {
            calendar.add(Calendar.HOUR_OF_DAY, -2);
            count(new Date(calendar.getTimeInMillis()));
        }
    }

    private void count(Date date) {
        StatModel stat = statDao.find(date);
        if (stat == null) {
            stat = new StatModel();
            stat.setDate(date);
        }
        stat.setCount(userService.count());
        stat.setRegister(userService.count(date));
        stat.setOnline(Math.max(stat.getOnline(), onlineService.count(date, 90)));
        statDao.save(stat);
    }
}
