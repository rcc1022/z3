package com.desert.eagle.control;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(ControlModel.NAME + ".dao")
class ControlDaoImpl implements ControlDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<ControlModel> query(int mode, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(ControlModel.class).where("c_mode=?").size(pageSize).page(pageNum), new Object[]{mode});
    }

    @Override
    public ControlModel findById(String id) {
        return liteOrm.findById(ControlModel.class, id);
    }

    @Override
    public ControlModel find(int mode, int type) {
        return liteOrm.findOne(new LiteQuery(ControlModel.class).where("c_mode=? and c_type=?"), new Object[]{mode, type});
    }

    @Override
    public void save(ControlModel control) {
        liteOrm.save(control);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(ControlModel.class, id);
    }
}