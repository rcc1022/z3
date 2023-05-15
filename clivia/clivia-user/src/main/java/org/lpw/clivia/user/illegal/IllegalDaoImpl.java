package org.lpw.clivia.user.illegal;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(IllegalModel.NAME + ".dao")
class IllegalDaoImpl implements IllegalDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<IllegalModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(IllegalModel.class).order("c_time DESC").size(pageSize).page(pageNum), null);
    }

    @Override
    public void save(IllegalModel illegal) {
        liteOrm.save(illegal);
    }
}