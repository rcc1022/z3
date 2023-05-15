package org.lpw.clivia.user.upload;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(UploadModel.NAME + ".dao")
class UploadDaoImpl implements UploadDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<UploadModel> query(String user, int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(UploadModel.class).where("c_user=?").order("c_time DESC").size(pageSize).page(pageNum), new Object[]{user});
    }

    @Override
    public UploadModel findById(String id) {
        return liteOrm.findById(UploadModel.class, id);
    }

    @Override
    public void save(UploadModel upload) {
        liteOrm.save(upload);
    }

    @Override
    public void delete(String id, String user) {
        liteOrm.delete(new LiteQuery(UploadModel.class).where("c_id=? and c_user=?"), new Object[]{id, user});
    }
}