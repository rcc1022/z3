package org.lpw.clivia.chat;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(ChatModel.NAME + ".dao")
class ChatDaoImpl implements ChatDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<ChatModel> query(String group, long time, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(ChatModel.class).where("c_group=? and c_time>?")
                .order("c_time desc").size(pageSize).page(pageNum), new Object[]{group, time});
    }

    @Override
    public void save(ChatModel chat) {
        liteOrm.save(chat);
    }

    @Override
    public void delete(long time) {
        liteOrm.delete(new LiteQuery(ChatModel.class).where("c_time<?"), new Object[]{time});
    }
}