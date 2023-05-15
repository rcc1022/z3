package org.lpw.clivia.payment;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(PaymentModel.NAME + ".dao")
class PaymentDaoImpl implements PaymentDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<PaymentModel> query(String type, String appId, String user, String orderNo, String billNo, String tradeNo,
                                        int state, String start, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().between("c_start", ColumnType.Timestamp, start)
                .where("c_type", DaoOperation.Equals, type)
                .where("c_app_id", DaoOperation.Equals, appId)
                .where("c_user", DaoOperation.Equals, user)
                .where("c_order_no", DaoOperation.Equals, orderNo)
                .where("c_bill_no", DaoOperation.Equals, billNo)
                .where("c_trade_no", DaoOperation.Equals, tradeNo)
                .where("c_state", DaoOperation.Equals, state)
                .order("c_start desc")
                .query(PaymentModel.class, pageSize, pageNum);
    }

    @Override
    public PaymentModel findById(String id) {
        return liteOrm.findById(PaymentModel.class, id);
    }

    @Override
    public PaymentModel findByOrderNo(String orderNo) {
        return liteOrm.findOne(new LiteQuery(PaymentModel.class).where("c_order_no=?"), new Object[]{orderNo});
    }

    @Override
    public void save(PaymentModel payment) {
        liteOrm.save(payment);
        liteOrm.close();
    }

    @Override
    public void delete(String user) {
        liteOrm.delete(new LiteQuery(PaymentModel.class).where("c_user=?"), new Object[]{user});
    }
}
