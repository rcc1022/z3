package com.desert.eagle.channel;

import org.lpw.photon.dao.orm.PageList;

interface ChannelDao {
    PageList<ChannelModel> query(int pageSize, int pageNum);

    ChannelModel findById(String id);

    void save(ChannelModel channel);

    void delete(String id);
}