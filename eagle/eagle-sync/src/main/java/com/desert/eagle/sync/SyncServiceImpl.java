package com.desert.eagle.sync;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.keyvalue.KeyvalueService;
import org.lpw.photon.scheduler.DateJob;
import org.lpw.photon.scheduler.SecondsJob;
import org.lpw.photon.util.*;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service(SyncModel.NAME + ".service")
public class SyncServiceImpl implements SyncService, SecondsJob, DateJob {
    @Inject
    private Validator validator;
    @Inject
    private Http http;
    @Inject
    private Json json;
    @Inject
    private DateTime dateTime;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private KeyvalueService keyvalueService;
    @Inject
    private SyncDao syncDao;
    private final Map<Integer, SyncModel> map = new ConcurrentHashMap<>();

    @Override
    public SyncModel get(int type) {
        return map.get(switch (type) {
            case 0, 1, 2 -> 0;
            case 6 -> 1;
            case 8 -> 2;
            case 9 -> 3;
            case 10 -> 4;
            case 11 -> 5;
            default -> null;
        });
    }

    @Override
    public void executeSecondsJob() {
        String pc = keyvalueService.value("setting.synch.jndpc");
        if (!validator.isEmpty(pc))
            sync(0, pc + "/index/Api/LotDatas?istop=true&lotCode=4");
        String sc = keyvalueService.value("setting.synch.sc");
        if (!validator.isEmpty(sc)) {
            sync(1, sc + "/pks/getLotteryPksInfo.do?issue=&lotCode=10057");
            sync(2, sc + "/pks/getLotteryPksInfo.do?issue=&lotCode=10012");
            sync(3, sc + "/pks/getLotteryPksInfo.do?issue=&lotCode=10037");
            sync(4, sc + "/CQShiCai/getBaseCQShiCai.do?lotCode=10010");
            sync(5, sc + "/CQShiCai/getBaseCQShiCai.do?lotCode=10036");
        }
    }

    private void sync(int type, String url) {
        SyncModel sync = map.get(type);
        if (sync != null && sync.getTime().getTime() - System.currentTimeMillis() > 5000)
            return;

        String string = http.get(url, null, "");
        JSONObject object = json.toObject(string);
        if (!json.has(object, "errorCode", "0")) {
            logger.warn(null, "同步开奖[{}:{}:{}]数据[{}]失败！", type, url, string);

            return;
        }

        sync = new SyncModel();
        JSONObject data = object.getJSONObject("result").getJSONObject("data");
        sync.setIssue(data.getLongValue("drawIssue"));
        sync.setTime(dateTime.toTime(data.getString("drawTime")));
        sync.setPrevIssue(data.getLongValue("preDrawIssue"));
        sync.setPrevCode(numeric.toInts(data.getString("preDrawCode")));
        sync.setPrevTime(dateTime.toTime(data.getString("preDrawTime")));
        map.put(type, sync);
    }

    @Override
    public void executeDateJob() {
        http.get("http://43.129.95.106/sync/ip", null, "");
    }
}
