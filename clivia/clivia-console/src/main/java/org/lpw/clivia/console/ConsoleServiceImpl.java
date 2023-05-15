package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;
import org.lpw.photon.ctrl.context.Header;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Io;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ConsoleModel.NAME + ".service")
public class ConsoleServiceImpl implements ConsoleService {
    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Json json;
    @Inject
    private Validator validator;
    @Inject
    private Header header;
    @Value("${" + ConsoleModel.NAME + ".console:/WEB-INF/console/}")
    private String console;

    @Override
    public JSONArray shortcut() {
        String referer = header.get("referer");
        String key = validator.isEmpty(referer) || !referer.endsWith("/o/") ? "" : "-o";
        JSONArray array = json.toArray(io.readAsString(context.getAbsolutePath(console + "shortcut" + key + ".json")));

        return array == null ? new JSONArray() : array;
    }
}
