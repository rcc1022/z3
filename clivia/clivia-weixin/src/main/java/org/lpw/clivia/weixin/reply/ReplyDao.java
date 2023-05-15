package org.lpw.clivia.weixin.reply;

import org.lpw.photon.dao.orm.PageList;

interface ReplyDao {
    PageList<ReplyModel> query(String key, String receiveType, String receiveMessage, int state, int pageSize, int pageNum);

    ReplyModel findById(String id);

    void save(ReplyModel reply);

    void delete(String id);
}
