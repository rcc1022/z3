package org.lpw.clivia.group.friend;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.sql.Timestamp;

@Repository(FriendModel.NAME + ".dao")
class FriendDaoImpl implements FriendDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<FriendModel> query(String user, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_user", DaoOperation.Equals, user).order("c_time desc")
                .query(FriendModel.class, pageSize, pageNum);
    }

    @Override
    public FriendModel findById(String id) {
        return liteOrm.findById(FriendModel.class, id);
    }

    @Override
    public FriendModel find(String user, String proposer) {
        return liteOrm.findOne(new LiteQuery(FriendModel.class).where("c_user=? and c_proposer=?"),
                new Object[]{user, proposer});
    }

    @Override
    public void save(FriendModel friend) {
        liteOrm.save(friend);
    }

    @Override
    public void state(int oldState, int newState, Timestamp time) {
        liteOrm.update(new LiteQuery(FriendModel.class).set("c_state=?").where("c_state=? and c_time<?"),
                new Object[]{newState, oldState, time});
    }

    @Override
    public void delete(FriendModel friend) {
        liteOrm.delete(friend);
    }

    @Override
    public void delete(String user) {
        liteOrm.delete(new LiteQuery(FriendModel.class).where("c_user=? or c_proposer=?"), new Object[]{user, user});
    }
}