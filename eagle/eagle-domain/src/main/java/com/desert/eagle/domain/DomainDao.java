package com.desert.eagle.domain;

import org.lpw.photon.dao.orm.PageList;

interface DomainDao {
    PageList<DomainModel> query(int type, int pageSize, int pageNum);

    DomainModel findById(String id);

    DomainModel find(int type, int status);

    void save(DomainModel domain);

    void status(String id, int status);

    void delete(String id);
}