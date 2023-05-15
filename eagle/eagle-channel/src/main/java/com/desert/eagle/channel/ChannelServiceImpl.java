package com.desert.eagle.channel;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(ChannelModel.NAME + ".service")
public class ChannelServiceImpl implements ChannelService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private ChannelDao channelDao;

    @Override
    public JSONObject query() {
        return channelDao.query(pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(ChannelModel channel) {
        ChannelModel model = validator.isId(channel.getId()) ? channelDao.findById(channel.getId()) : null;
        if (model == null)
            channel.setId(null);
        channelDao.save(channel);
    }

    @Override
    public void delete(String id) {
        channelDao.delete(id);
    }
}
