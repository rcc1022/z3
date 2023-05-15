package org.lpw.clivia.chrome;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

public interface ChromeService {
    /**
     * 引用KEY是否存在验证器Bean名称。
     */
    String VALIDATOR_KEY_EXISTS = ChromeModel.NAME + ".validator.key.exists";

    JSONObject query(String key, String name);

    ChromeModel findByKey(String key);

    JSONObject save(ChromeModel chrome);

    void delete(String id);

    String pdf(String key, String url, int width, int height, String pages, int wait, Map<String, String> map);

    byte[] pdf(String key, String url, int width, int height, String pages, int wait);

    byte[] png(String key, String url, int x, int y, int width, int height, int wait);

    byte[] jpg(String key, String url, int x, int y, int width, int height, int wait);
}
