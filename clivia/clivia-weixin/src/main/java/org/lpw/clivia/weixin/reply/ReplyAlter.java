package org.lpw.clivia.weixin.reply;

import org.lpw.clivia.weixin.WeixinModel;

import java.util.Map;

/**
 * 回复修改器。
 *
 */
public interface ReplyAlter {
    /**
     * 修改回复。
     *
     * @param weixin 微信配置。
     * @param map    参数集。
     */
    void alter(WeixinModel weixin, Map<String, String> map);

    /**
     * 修改回复。
     *
     * @param weixin 微信配置。
     * @param map    参数集。
     * @param reply  回复。
     */
    void alter(WeixinModel weixin, Map<String, String> map, ReplyModel reply);
}
