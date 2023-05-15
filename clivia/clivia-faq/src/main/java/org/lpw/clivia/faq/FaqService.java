package org.lpw.clivia.faq;

import com.alibaba.fastjson.JSONObject;

public interface FaqService {
    JSONObject query(String key);

    JSONObject get(String id);

    void save(FaqModel faq);

    void delete(String id);
}
