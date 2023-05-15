package com.desert.eagle.wunum;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(WunumModel.NAME + ".dao")
class WunumDaoImpl implements WunumDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<WunumModel> query(int type, String issue, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_type", DaoOperation.Equals, type)
                .where("c_issue", DaoOperation.Equals, issue)
                .order("c_time DESC")
                .query(WunumModel.class, pageSize, pageNum);
    }

    @Override
    public WunumModel findById(String id) {
        return liteOrm.findById(WunumModel.class, id);
    }

    @Override
    public WunumModel find(int type, long issue) {
        return liteOrm.findOne(new LiteQuery(WunumModel.class).where("c_type=? and c_issue=?"), new Object[]{type, issue});
    }

    @Override
    public WunumModel latest(int type, int status) {
        return liteOrm.findOne(new LiteQuery(WunumModel.class).where("c_type=? and c_status=?").order("c_issue desc"), new Object[]{type, status});
    }

    @Override
    public void save(WunumModel wunum) {
        liteOrm.save(wunum);
        liteOrm.close();
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(WunumModel.class, id);
    }

    @Override
    public void delete(Timestamp time) {
        liteOrm.delete(new LiteQuery(WunumModel.class).where("c_time<?"), new Object[]{time});
        liteOrm.close();
    }
}