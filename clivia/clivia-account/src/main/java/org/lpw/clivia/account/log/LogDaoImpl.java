package org.lpw.clivia.account.log;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(LogModel.NAME + ".dao")
class LogDaoImpl implements LogDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<LogModel> query(String user, String owner, String type, String channel, int state, String start, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_user", DaoOperation.Equals, user)
                .where("c_owner", DaoOperation.Equals, owner)
                .where("c_type", DaoOperation.Equals, type)
                .where("c_channel", DaoOperation.Equals, channel)
                .where("c_state", DaoOperation.Equals, state)
                .between("c_start", ColumnType.Timestamp, start)
                .order("c_start desc,c_index desc")
                .query(LogModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<LogModel> query(int restate) {
        return liteOrm.query(new LiteQuery(LogModel.class).where("c_restate=?"), new Object[]{restate});
    }

    @Override
    public LogModel findById(String id) {
        return liteOrm.findById(LogModel.class, id);
    }

    @Override
    public void save(LogModel log) {
        liteOrm.save(log);
        liteOrm.close();
    }

    @Override
    public void delete(String user) {
        liteOrm.delete(new LiteQuery(LogModel.class).where("c_user=?"), new Object[]{user});
    }
}
