package org.lpw.clivia.transfer;

import com.alibaba.fastjson.JSONObject;

/**
 * 转账完成通知。
 *
 */
public interface TransferNotice {
    /**
     * 转账完成。
     *
     * @param transfer 转账信息。
     * @param notice   通知配置。
     */
    void transferDone(TransferModel transfer, JSONObject notice);
}
