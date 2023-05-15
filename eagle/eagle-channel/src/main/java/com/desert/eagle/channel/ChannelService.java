package com.desert.eagle.channel;

import com.alibaba.fastjson.JSONObject;

public interface ChannelService {

    JSONObject query();

    void save(ChannelModel channel);

    void delete(String id);
}
