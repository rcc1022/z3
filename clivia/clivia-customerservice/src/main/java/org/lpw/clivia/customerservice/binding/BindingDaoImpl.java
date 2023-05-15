package org.lpw.clivia.customerservice.binding;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(BindingModel.NAME + ".dao")
class BindingDaoImpl implements BindingDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public BindingModel find(String user, String type, int state) {
        return liteOrm.findOne(new LiteQuery(BindingModel.class).where("c_user=? and c_type=? and c_state=?").order("c_time desc"), new Object[]{user, type, state});
    }

    @Override
    public void save(BindingModel binding) {
        liteOrm.save(binding);
    }

    @Override
    public void type(String customerservice, String type) {
        liteOrm.update(new LiteQuery(BindingModel.class).set("c_type=?").where("c_customerservice=?"), new Object[]{type, customerservice});
    }

    @Override
    public void state(String customerservice, int state) {
        liteOrm.update(new LiteQuery(BindingModel.class).set("c_state=?").where("c_customerservice=?"), new Object[]{state, customerservice});
    }

    @Override
    public void delete(String customerservice) {
        liteOrm.delete(new LiteQuery(BindingModel.class).where("c_customerservice=?"), new Object[]{customerservice});
    }
}
