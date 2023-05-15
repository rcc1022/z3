package org.lpw.clivia.weixin.media;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(MediaModel.NAME + ".dao")
class MediaDaoImpl implements MediaDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<MediaModel> query(String key, String appId, String type, String name, String time, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_key", DaoOperation.Equals, key)
                .where("c_app_id", DaoOperation.Equals, appId)
                .where("c_type", DaoOperation.Equals, type)
                .between("c_time", ColumnType.Timestamp, time)
                .like(null, "c_name", name)
                .order("c_time desc")
                .query(MediaModel.class, pageSize, pageNum);
    }

    @Override
    public MediaModel findById(String id) {
        return liteOrm.findById(MediaModel.class, id);
    }

    @Override
    public void save(MediaModel media) {
        liteOrm.save(media);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(MediaModel.class, id);
    }
}
