package org.lpw.clivia.async;

import com.alibaba.fastjson.JSONObject;

import java.util.concurrent.Callable;

public interface AsyncService {
    /**
     * 提交。
     *
     * @param key       引用KEY。
     * @param parameter 参数。
     * @param timeout   超时时长，单位：秒。
     * @param callable  执行处理器。
     * @return 异步ID。
     */
    String submit(String key, String parameter, int timeout, Callable<String> callable);

    /**
     * 提交。
     *
     * @param key           引用KEY。
     * @param parameter     参数。
     * @param timeout       超时时长，单位：秒。
     * @param callable      执行处理器。
     * @param asyncNotifier 通知器。
     * @return 异步ID。
     */
    String submit(String key, String parameter, int timeout, Callable<String> callable, AsyncNotifier asyncNotifier);

    /**
     * 查询。
     *
     * @param id ID值。
     * @return 执行状态与结果。
     */
    JSONObject find(String id);

    /**
     * 等待结果。
     *
     * @param id   ID值。
     * @param time 等待时长，单位：秒。
     * @return 执行结果，失败返回null。
     */
    String wait(String id, int time);
}
