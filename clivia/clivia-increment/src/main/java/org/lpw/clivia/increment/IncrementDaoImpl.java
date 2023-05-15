package org.lpw.clivia.increment;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(IncrementModel.NAME + ".dao")
class IncrementDaoImpl implements IncrementDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public IncrementModel find(String key) {
        return liteOrm.findOne(new LiteQuery(IncrementModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void save(IncrementModel increment) {
        liteOrm.save(increment);
        liteOrm.close();
    }
}
