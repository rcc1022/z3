package com.desert.eagle.message;

import org.lpw.photon.dao.orm.PageList;

interface MessageDao {
    PageList<MessageModel> query(String game, long time, int pageSize, int pageNum);

    void save(MessageModel message);
}