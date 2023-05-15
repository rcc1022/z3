package com.desert.eagle.control;

import org.lpw.photon.dao.orm.PageList;

interface ControlDao {
    PageList<ControlModel> query(int mode, int pageSize, int pageNum);

    ControlModel findById(String id);

    ControlModel find(int mode, int type);

    void save(ControlModel control);

    void delete(String id);
}