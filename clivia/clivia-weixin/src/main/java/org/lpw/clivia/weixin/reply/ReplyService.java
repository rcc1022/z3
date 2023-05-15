package org.lpw.clivia.weixin.reply;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.weixin.WeixinModel;

public interface ReplyService {
    /**
     * 检索。
     *
     * @param key            引用key。
     * @param receiveType    接收类型。
     * @param receiveMessage 接收消息。
     * @param state          状态：-1-全部；0-待使用；1-使用中。
     * @return 回复信息集。
     */
    JSONObject query(String key, String receiveType, String receiveMessage, int state);

    /**
     * 保存。
     *
     * @param reply 回复。
     */
    void save(ReplyModel reply);

    /**
     * 删除。
     *
     * @param id ID值。
     */
    void delete(String id);

    /**
     * 回复。
     *
     * @param weixin         微信配置。
     * @param openId         用户Open ID。
     * @param receiveType    接收类型。
     * @param receiveMessage 接收消息。
     * @param eventKey       事件key。
     */
    void send(WeixinModel weixin, String openId, String receiveType, String receiveMessage, String eventKey);
}
