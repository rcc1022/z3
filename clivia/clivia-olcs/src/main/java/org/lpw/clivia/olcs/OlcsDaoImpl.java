package org.lpw.clivia.olcs;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(OlcsModel.NAME + ".dao")
class OlcsDaoImpl implements OlcsDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<OlcsModel> query(String user, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(OlcsModel.class).where("c_user=?").order("c_time desc").size(pageSize).page(pageNum), new Object[]{user});
    }

    @Override
    public PageList<OlcsModel> query(String user, Timestamp time) {
        return liteOrm.query(new LiteQuery(OlcsModel.class).where("c_user=? and c_time>?").order("c_time"), new Object[]{user, time});
    }

    @Override
    public OlcsModel findById(String id) {
        return liteOrm.findById(OlcsModel.class, id);
    }

    @Override
    public void save(OlcsModel olcs) {
        liteOrm.save(olcs);
    }

    @Override
    public void delete(String user) {
        liteOrm.delete(new LiteQuery(OlcsModel.class).where("c_user=?"), new Object[]{user});
    }

    @Override
    public void delete(Timestamp time) {
        liteOrm.delete(new LiteQuery(OlcsModel.class).where("c_time<?"), new Object[]{time});
    }
}