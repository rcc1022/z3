package org.lpw.clivia.device;

import org.lpw.photon.dao.orm.PageList;

interface DeviceDao {
    PageList<DeviceModel> query(int pageSize, int pageNum);

    PageList<DeviceModel> query(String user);

    DeviceModel find(String sid);

    void save(DeviceModel device);

    void delete(String sid);

    void deletes(String user);
}