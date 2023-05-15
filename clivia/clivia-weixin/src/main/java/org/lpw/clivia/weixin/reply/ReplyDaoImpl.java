package org.lpw.clivia.weixin.reply;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(ReplyModel.NAME + ".dao")
class ReplyDaoImpl implements ReplyDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<ReplyModel> query(String key, String receiveType, String receiveMessage, int state, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_key", DaoOperation.Equals, key)
                .where("c_receive_type", DaoOperation.Equals, receiveType)
                .where("c_receive_message", DaoOperation.Equals, receiveMessage)
                .where("c_state", DaoOperation.Equals, state)
                .order("c_key,c_receive_type,c_receive_message,c_sort")
                .query(ReplyModel.class, pageSize, pageNum);
    }

    @Override
    public ReplyModel findById(String id) {
        return liteOrm.findById(ReplyModel.class, id);
    }

    @Override
    public void save(ReplyModel reply) {
        liteOrm.save(reply);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(ReplyModel.class, id);
    }
}
