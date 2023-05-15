package com.desert.eagle.player.commission;

import org.lpw.photon.dao.orm.PageList;

interface CommissionDao {
    PageList<CommissionModel> query(int pageSize, int pageNum);

    CommissionModel findById(String id);

    void save(CommissionModel commission);
}