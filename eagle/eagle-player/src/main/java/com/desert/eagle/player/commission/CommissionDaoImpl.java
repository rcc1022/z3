package com.desert.eagle.player.commission;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(CommissionModel.NAME + ".dao")
class CommissionDaoImpl implements CommissionDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<CommissionModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(CommissionModel.class).order("c_submit DESC").size(pageSize).page(pageNum), null);
    }

    @Override
    public CommissionModel findById(String id) {
        return liteOrm.findById(CommissionModel.class, id);
    }

    @Override
    public void save(CommissionModel commission) {
        liteOrm.save(commission);
    }
}