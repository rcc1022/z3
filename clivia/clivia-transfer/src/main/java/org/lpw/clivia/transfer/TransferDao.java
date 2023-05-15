package org.lpw.clivia.transfer;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface TransferDao {
    PageList<TransferModel> query(String type, String appId, String user, String orderNo, String billNo, String tradeNo, int state, String start, int pageSize, int pageNum);

    PageList<TransferModel> query(int state, Timestamp start);

    TransferModel findById(String id);

    TransferModel findByOrderNo(String orderNo);

    void save(TransferModel transfer);

    void delete(String user);
}
