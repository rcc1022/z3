package org.lpw.clivia.push;

import com.alibaba.fastjson.JSONObject;

public interface PushSender {
    /**
     * 引用KEY。
     *
     * @return 引用KEY。
     */
    String key();

    /**
     * 名称。
     *
     * @return 名称。
     */
    String name();

    /**
     * 推送。
     *
     * @param push   设置。
     * @param config 配置。
     * @param args   参数集。
     * @return 推送结果，null-失败；空JSON-成功；非空JSON-包含错误码和错误信息。
     */
    Object push(PushModel push, JSONObject config, JSONObject args);
}
