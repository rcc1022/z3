package com.desert.eagle.scnum;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(ScnumModel.NAME + ".dao")
class ScnumDaoImpl implements ScnumDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<ScnumModel> query(int type, String issue, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_type", DaoOperation.Equals, type)
                .where("c_issue", DaoOperation.Equals, issue)
                .order("c_time DESC")
                .query(ScnumModel.class, pageSize, pageNum);
    }

    @Override
    public ScnumModel findById(String id) {
        return liteOrm.findById(ScnumModel.class, id);
    }

    @Override
    public ScnumModel find(int type, long issue) {
        return liteOrm.findOne(new LiteQuery(ScnumModel.class).where("c_type=? and c_issue=?"), new Object[]{type, issue});
    }

    @Override
    public ScnumModel latest(int type, int status) {
        return liteOrm.findOne(new LiteQuery(ScnumModel.class).where("c_type=? and c_status=?").order("c_issue desc"), new Object[]{type, status});
    }

    @Override
    public void save(ScnumModel scnum) {
        liteOrm.save(scnum);
        liteOrm.close();
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(ScnumModel.class, id);
    }

    @Override
    public void delete(Timestamp time) {
        liteOrm.delete(new LiteQuery(ScnumModel.class).where("c_time<?"), new Object[]{time});
        liteOrm.close();
    }
}