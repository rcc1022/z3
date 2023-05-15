package com.desert.eagle.channel;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(ChannelModel.NAME + ".dao")
class ChannelDaoImpl implements ChannelDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<ChannelModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(ChannelModel.class).order("c_sort").size(pageSize).page(pageNum), null);
    }

    @Override
    public ChannelModel findById(String id) {
        return liteOrm.findById(ChannelModel.class, id);
    }

    @Override
    public void save(ChannelModel channel) {
        liteOrm.save(channel);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(ChannelModel.class, id);
    }
}