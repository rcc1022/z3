package org.lpw.clivia.transfer;

/**
 * 转账监听器。
 *
 */
public interface TransferListener {
    /**
     * 获取转账类型。
     *
     * @return 付款类型。
     */
    String getTransferType();

    /**
     * 重置转账订单状态。
     *
     * @param transfer 订单信息。
     */
    void resetTransferState(TransferModel transfer);
}
