package org.lpw.clivia.device;

import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(DeviceModel.NAME + ".dao")
class DeviceDaoImpl implements DeviceDao {
    @Inject
    private LiteOrm liteOrm;

    @Override
    public PageList<DeviceModel> query(int pageSize, int pageNum) {
        return liteOrm.query(new LiteQuery(DeviceModel.class).size(pageSize).page(pageNum), null);
    }

    @Override
    public PageList<DeviceModel> query(String user) {
        return liteOrm.query(new LiteQuery(DeviceModel.class).where("c_user=?"), new Object[]{user});
    }

    @Override
    public DeviceModel find(String sid) {
        return liteOrm.findOne(new LiteQuery(DeviceModel.class).where("c_sid=?"), new Object[]{sid});
    }

    @Override
    public void save(DeviceModel device) {
        liteOrm.save(device);
    }

    @Override
    public void delete(String sid) {
        liteOrm.delete(new LiteQuery(DeviceModel.class).where("c_sid=?"), new Object[]{sid});
    }

    @Override
    public void deletes(String user) {
        liteOrm.delete(new LiteQuery(DeviceModel.class).where("c_user=?"), new Object[]{user});
    }
}