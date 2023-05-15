package org.lpw.clivia.weixin.info;

import org.lpw.photon.dao.orm.PageList;

import java.sql.Timestamp;

interface InfoDao {
    PageList<InfoModel> query(Timestamp time);

    PageList<InfoModel> query(String unionId);

    InfoModel find(String openId);

    InfoModel find(String appId, String unionId);

    void save(InfoModel info);
}
