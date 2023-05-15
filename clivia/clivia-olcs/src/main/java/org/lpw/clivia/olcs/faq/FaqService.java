package org.lpw.clivia.olcs.faq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public interface FaqService {
    JSONObject query(String subject, String content, int frequently);

    JSONArray frequently();

    FaqModel find(String subject);

    void save(FaqModel faq);

    void delete(String id);
}
