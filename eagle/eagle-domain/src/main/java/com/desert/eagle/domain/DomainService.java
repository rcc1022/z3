package com.desert.eagle.domain;

import com.alibaba.fastjson.JSONObject;

public interface DomainService {
    JSONObject query(int type);

    JSONObject entrance();

    String video();

    DomainModel find(int type, int status);

    String invite(String uid);

    void creates(int type, String[] names, int status);

    void save(DomainModel domain);

    void status(String id, int status);

    void delete(String id);
}
