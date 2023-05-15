package org.lpw.clivia.customerservice;

import org.lpw.photon.dao.orm.PageList;

interface CustomerserviceDao {
    PageList<CustomerserviceModel> query(String type, int state, int pageSize, int pageNum);

    PageList<CustomerserviceModel> query(int state);

    CustomerserviceModel findById(String id);

    void save(CustomerserviceModel customerservice);

    void state(String id, int state);

    void delete(String id);
}
