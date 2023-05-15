package com.desert.eagle.pcnum;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(PcnumModel.NAME + ".dao")
class PcnumDaoImpl implements PcnumDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<PcnumModel> query(int type, String issue, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_type", DaoOperation.Equals, type)
                .where("c_issue", DaoOperation.Equals, issue)
                .order("c_time DESC")
                .query(PcnumModel.class, pageSize, pageNum);
    }

    @Override
    public PcnumModel findById(String id) {
        return liteOrm.findById(PcnumModel.class, id);
    }

    @Override
    public PcnumModel find(int type, long issue) {
        return liteOrm.findOne(new LiteQuery(PcnumModel.class).where("c_type=? and c_issue=?"), new Object[]{type, issue});
    }

    @Override
    public PcnumModel latest(int type, int status) {
        return liteOrm.findOne(new LiteQuery(PcnumModel.class).where("c_type=? and c_status=?").order("c_issue desc"), new Object[]{type, status});
    }

    @Override
    public void save(PcnumModel pcnum) {
        liteOrm.save(pcnum);
        liteOrm.close();
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(PcnumModel.class, id);
    }

    @Override
    public void delete(Timestamp time) {
        liteOrm.delete(new LiteQuery(PcnumModel.class).where("c_time<?"), new Object[]{time});
        liteOrm.close();
    }
}