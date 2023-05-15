package com.desert.eagle.domain;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(DomainModel.NAME + ".dao")
class DomainDaoImpl implements DomainDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<DomainModel> query(int type, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(DomainModel.class).where("c_type=?").order("c_status desc,c_modify desc").size(pageSize).page(pageNum), new Object[]{type});
    }

    @Override
    public DomainModel findById(String id) {
        return liteOrm.findById(DomainModel.class, id);
    }

    @Override
    public DomainModel find(int type, int status) {
        return liteOrm.findOne(new LiteQuery(DomainModel.class).where("c_type=? and c_status=?").order("c_modify desc"), new Object[]{type, status});
    }

    @Override
    public void save(DomainModel domain) {
        liteOrm.save(domain);
    }

    @Override
    public void status(String id, int status) {
        liteOrm.update(new LiteQuery(DomainModel.class).set("c_status=?").where("c_id=?"), new Object[]{status, id});
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(DomainModel.class, id);
    }
}