package org.lpw.clivia.module;

import com.alibaba.fastjson.JSONObject;

public interface ModuleService {
    String VALIDATOR_EXISTS = ModuleModel.NAME + ".validator.exists";

    JSONObject query();

    void save(ModuleModel module);

    void generate(String id);

    void delete(String id);
}
