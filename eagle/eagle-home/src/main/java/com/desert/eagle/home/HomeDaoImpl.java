package com.desert.eagle.home;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(HomeModel.NAME + ".dao")
class HomeDaoImpl implements HomeDao {
    @Inject
    private LiteOrm liteOrm;
}