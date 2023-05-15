package org.lpw.clivia.editor;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.ctrl.context.Session;
import org.lpw.photon.scheduler.HourJob;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.TimeUnit;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(EditorModel.NAME + ".service")
public class EditorServiceImpl implements EditorService, SecondsJob, HourJob {
    @Inject
    private Validator validator;
    @Inject
    private Generator generator;
    @Inject
    private Session session;
    private final Map<String, Editing> map = new ConcurrentHashMap<>();

    @Override
    public void put(String key, JSONArray array, EditorListener listener) {
        map.computeIfAbsent(key, k -> new Editing(validator, generator, key, array, listener));
    }

    @Override
    public JSONArray get(String key) {
        if (map.containsKey(key))
            return map.get(key).get();

        if (key.equals("editor")) {
            put(key, null, null);

            return map.get(key).get();
        }

        return new JSONArray();
    }

    @Override
    public JSONObject save(String key, String id, JSONArray lines, long sync) {
        if (!map.containsKey(key))
            return new JSONObject();

        return map.get(key).put(id, lines, sync);
    }

    @Override
    public void executeSecondsJob() {
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.SECOND) % 5 > 0)
            return;

        long time = calendar.getTimeInMillis() - TimeUnit.Minute.getTime();
        map.values().forEach(editing -> editing.save(time));
    }

    @Override
    public void executeHourJob() {
        long time = System.currentTimeMillis() - TimeUnit.Day.getTime();
        map.forEach((key, editing) -> {
            if (editing.overdue(time))
                map.remove(key);
        });
    }
}
