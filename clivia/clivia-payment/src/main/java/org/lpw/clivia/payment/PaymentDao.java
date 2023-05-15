package org.lpw.clivia.payment;

import org.lpw.photon.dao.orm.PageList;

interface PaymentDao {
    PageList<PaymentModel> query(String type, String appId, String user, String orderNo, String billNo, String tradeNo, int state, String start, int pageSize, int pageNum);

    PaymentModel findById(String id);

    PaymentModel findByOrderNo(String orderNo);

    void save(PaymentModel payment);

    void delete(String user);
}
