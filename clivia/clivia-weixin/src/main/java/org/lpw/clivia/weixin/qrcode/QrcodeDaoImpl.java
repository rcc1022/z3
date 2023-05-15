package org.lpw.clivia.weixin.qrcode;

import org.lpw.clivia.dao.ColumnType;
import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(QrcodeModel.NAME + ".dao")
class QrcodeDaoImpl implements QrcodeDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<QrcodeModel> query(String key, String appId, String user, String name, String scene, String time, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_key", DaoOperation.Equals, key)
                .where("c_app_id", DaoOperation.Equals, appId)
                .where("c_user", DaoOperation.Equals, user)
                .between("c_time", ColumnType.Timestamp, time)
                .like(null, "c_name", name)
                .like(null, "c_scene", scene)
                .order("c_time desc")
                .query(QrcodeModel.class, pageSize, pageNum);
    }

    @Override
    public QrcodeModel find(String key, String user, String name) {
        return liteOrm.findOne(new LiteQuery(QrcodeModel.class).where("c_user=? and c_name=? and c_key=?").order("c_time desc"),
                new Object[]{user, name, key});
    }

    @Override
    public void save(QrcodeModel qrcode) {
        liteOrm.save(qrcode);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(QrcodeModel.class, id);
    }
}
