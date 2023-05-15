package org.lpw.clivia.customerservice;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(CustomerserviceModel.NAME + ".dao")
class CustomerserviceDaoImpl implements CustomerserviceDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<CustomerserviceModel> query(String type, int state, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_type", DaoOperation.Equals, type)
                .where("c_state", DaoOperation.Equals, state)
                .query(CustomerserviceModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<CustomerserviceModel> query(int state) {
        return liteOrm.query(new LiteQuery(CustomerserviceModel.class).where("c_state=?"), new Object[]{state});
    }

    @Override
    public CustomerserviceModel findById(String id) {
        return liteOrm.findById(CustomerserviceModel.class, id);
    }

    @Override
    public void save(CustomerserviceModel customerservice) {
        liteOrm.save(customerservice);
    }

    @Override
    public void state(String id, int state) {
        liteOrm.update(new LiteQuery(CustomerserviceModel.class).set("c_state=?").where("c_id=?"), new Object[]{state, id});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(CustomerserviceModel.class, id);
    }
}
