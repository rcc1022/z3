package org.lpw.clivia.push;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(PushModel.NAME + ".dao")
class PushDaoImpl implements PushDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<PushModel> query(String scene, String sender, String name, int state, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_sender", DaoOperation.Equals, sender)
                .where("c_state", DaoOperation.Equals, state)
                .like(null, "c_scene", scene)
                .like(null, "c_name", name)
                .order("c_time desc")
                .query(PushModel.class, pageSize, pageNum);
    }

    @Override
    public PushModel findById(String id) {
        return liteOrm.findById(PushModel.class, id);
    }

    @Override
    public PushModel find(String scene, int state) {
        return liteOrm.findOne(new LiteQuery(PushModel.class).where("c_scene=? and c_state=?").order("c_time"), new Object[]{scene, state});
    }

    @Override
    public void insert(PushModel push) {
        liteOrm.insert(push);
    }

    @Override
    public void save(PushModel push) {
        liteOrm.save(push);
    }

    @Override
    public void state(String id, int state) {
        liteOrm.update(new LiteQuery(PushModel.class).set("c_state=?").where("c_id=?"), new Object[]{state, id});
    }

    @Override
    public void time(String id, Timestamp time) {
        liteOrm.update(new LiteQuery(PushModel.class).set("c_time=?").where("c_id=?"), new Object[]{time, id});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(PushModel.class, id);
    }
}
