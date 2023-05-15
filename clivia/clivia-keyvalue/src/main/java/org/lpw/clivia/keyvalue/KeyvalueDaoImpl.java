package org.lpw.clivia.keyvalue;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(KeyvalueModel.NAME + ".dao")
class KeyvalueDaoImpl implements KeyvalueDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<KeyvalueModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(KeyvalueModel.class).order("c_key").size(pageSize).page(pageNum), null);
    }

    @Override
    public PageList<KeyvalueModel> query(String key, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(KeyvalueModel.class).where("c_key LIKE ?").order("c_key").size(pageSize).page(pageNum), new Object[]{key + "%"});
    }

    @Override
    public KeyvalueModel findById(String id) {
        return liteOrm.findById(KeyvalueModel.class, id);
    }

    @Override
    public KeyvalueModel findByKey(String key) {
        return liteOrm.findOne(new LiteQuery(KeyvalueModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void save(KeyvalueModel keyvalue) {
        liteOrm.save(keyvalue);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(KeyvalueModel.class, id);
    }
}
