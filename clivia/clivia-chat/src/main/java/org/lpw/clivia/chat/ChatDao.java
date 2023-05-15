package org.lpw.clivia.chat;

import org.lpw.photon.dao.orm.PageList;

interface ChatDao {
    PageList<ChatModel> query(String group, long time, int pageSize, int pageNum);

    void save(ChatModel chat);

    void delete(long time);
}