package org.lpw.clivia.user.online;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.jdbc.Sql;
import org.lpw.photon.dao.jdbc.SqlTable;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.util.Set;

@Repository(OnlineModel.NAME + ".dao")
class OnlineDaoImpl implements OnlineDao {
    @Inject
    private Sql sql;
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<OnlineModel> query(Set<String> user, String ip, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .in("c_user", user)
                .where("c_ip", DaoOperation.Equals, ip)
                .where("c_grade", DaoOperation.Less, 99)
                .order("c_last_visit desc")
                .query(OnlineModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<OnlineModel> query(Timestamp visit) {
        return liteOrm.query(new LiteQuery(OnlineModel.class).where("c_last_visit<?"), new Object[]{visit});
    }

    @Override
    public PageList<OnlineModel> query(String user) {
        return liteOrm.query(new LiteQuery(OnlineModel.class).where("c_user=?"), new Object[]{user});
    }

    @Override
    public OnlineModel findById(String id) {
        return liteOrm.findById(OnlineModel.class, id);
    }

    @Override
    public OnlineModel findBySid(String sid) {
        return liteOrm.findOne(new LiteQuery(OnlineModel.class).where("c_sid=?"), new Object[]{sid});
    }

    @Override
    public SqlTable user(Timestamp[] lastVisit, int grade) {
        return sql.query("select c_user from m_user_online where c_last_visit between ? and ? and c_grade<=?", new Object[]{lastVisit[0], lastVisit[1], grade});
    }

    @Override
    public SqlTable user(Timestamp lastVisit, int grade) {
        return sql.query("select c_user from m_user_online where c_last_visit>=? and c_grade<=?", new Object[]{lastVisit, grade});
    }

    @Override
    public void save(OnlineModel online) {
        liteOrm.save(online);
    }

    @Override
    public void lastVisit(String sid, Timestamp lastVisit) {
        liteOrm.update(new LiteQuery(OnlineModel.class).set("c_last_visit=?").where("c_sid=?"), new Object[]{lastVisit, sid});
    }

    @Override
    public void delete(OnlineModel online) {
        liteOrm.delete(online);
    }
}
