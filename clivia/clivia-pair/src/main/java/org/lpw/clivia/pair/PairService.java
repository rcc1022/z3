package org.lpw.clivia.pair;

import java.util.Set;
import java.util.function.Function;

import com.alibaba.fastjson.JSONObject;

public interface PairService {
    int count(String owner);

    int count(String owner, String value);

    Set<String> values(String owner);

    JSONObject query(String owner, boolean desc, int pageSize, int pageNum, Function<String, JSONObject> function);

    void save(String owner, String value);

    void delete(String owner, String value);
}
