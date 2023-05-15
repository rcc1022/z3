package com.desert.eagle.sync;

import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(SyncModel.NAME + ".dao")
class SyncDaoImpl implements SyncDao {
    @Inject
    private LiteOrm liteOrm;
}