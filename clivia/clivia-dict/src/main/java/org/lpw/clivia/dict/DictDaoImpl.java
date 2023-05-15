package org.lpw.clivia.dict;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(DictModel.NAME + ".dao")
class DictDaoImpl implements DictDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<DictModel> query(String key, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_key", DaoOperation.Equals, key).order("c_value")
                .query(DictModel.class, pageSize, pageNum);
    }

    @Override
    public DictModel findById(String id) {
        return liteOrm.findById(DictModel.class, id);
    }

    @Override
    public DictModel findByKeyValue(String key, String value) {
        return liteOrm.findOne(new LiteQuery(DictModel.class).where("c_key=? and c_value=?"),
                new Object[] { key, value });
    }

    @Override
    public void save(DictModel dict) {
        liteOrm.save(dict);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(DictModel.class, id);
    }
}