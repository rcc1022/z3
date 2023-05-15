package org.lpw.clivia.console;

import com.alibaba.fastjson.JSONArray;

public interface Dashboard {
    JSONArray get();

    JSONArray cards();
}
