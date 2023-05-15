package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.user.UserService;
import org.lpw.clivia.user.crosier.CrosierService;
import org.lpw.clivia.user.crosier.CrosierValid;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Numeric;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(ConsoleModel.NAME + ".dashboard")
public class DashboardImpl implements Dashboard, CrosierValid {
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private UserService userService;
    @Inject
    private CrosierService crosierService;
    @Value("${" + ConsoleModel.NAME + ".console:/WEB-INF/console/}")
    private String console;
    private JSONArray dashboard;
    private String cards;
    private final Map<String, JSONArray> map = new ConcurrentHashMap<>();

    @Override
    public JSONArray get() {
        return map.computeIfAbsent(numeric.toString(userService.grade(), "0"), key -> {
            if (dashboard == null)
                load();
            JSONArray array = new JSONArray();
            if (dashboard == null)
                return array;

            for (int i = 0, size = dashboard.size(); i < size; i++) {
                JSONObject column = dashboard.getJSONObject(i);
                JSONObject col = new JSONObject();
                col.put("span", column.getIntValue("span"));
                JSONArray cards = column.getJSONArray("cards");
                JSONArray cs = new JSONArray();
                for (int j = 0, s = cards.size(); j < s; j++) {
                    JSONObject card = cards.getJSONObject(j);
                    if (crosierService.permit(card.getString("service"), json.toMap(card.getJSONObject("parameter"))))
                        cs.add(card);
                }
                col.put("cards", cs);
                array.add(col);
            }

            return array;
        });
    }

    @Override
    public JSONArray cards() {
        if (cards == null)
            load();

        return json.toArray(cards);
    }

    @Override
    public void crosierValid(int grade) {
        map.remove(numeric.toString(grade, "0"));
    }

    private void load() {
        JSONArray array = json.toArray(io.readAsString(context.getAbsolutePath(console + "dashboard.json")));
        if (array == null) {
            logger.warn(null, "读取Dashboard配置失败！");

            return;
        }

        if (logger.isInfoEnable())
            logger.info("载入Dashboard配置{}。", array);

        dashboard = array;
        JSONArray cards = new JSONArray();
        for (int i = 0, size = dashboard.size(); i < size; i++)
            cards.addAll(dashboard.getJSONObject(i).getJSONArray("cards"));
        this.cards = cards.toString();
    }
}
