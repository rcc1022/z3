package com.desert.eagle.message;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(MessageModel.NAME + ".dao")
class MessageDaoImpl implements MessageDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<MessageModel> query(String game, long time, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(MessageModel.class).where("c_game=? and c_time>?").order("c_time desc").size(pageSize).size(pageSize), new Object[]{game, time});
    }

    @Override
    public void save(MessageModel message) {
        liteOrm.save(message);
    }
}