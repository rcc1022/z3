package org.lpw.clivia.user.inviter;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(InviterModel.NAME + ".dao")
class InviterDaoImpl implements InviterDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public InviterModel findByPsid(String psid) {
        return liteOrm.findOne(new LiteQuery(InviterModel.class).where("c_psid=?"), new Object[]{psid});
    }

    @Override
    public void save(InviterModel inviter) {
        liteOrm.save(inviter);
    }

    @Override
    public void clean(Timestamp time) {
        liteOrm.delete(new LiteQuery(InviterModel.class).where("c_time<?"), new Object[]{time});
    }
}
