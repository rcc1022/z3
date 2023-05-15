package org.lpw.clivia.weixin.media;

import org.lpw.photon.dao.orm.PageList;

interface MediaDao {
    PageList<MediaModel> query(String key, String appId, String type, String name, String time, int pageSize, int pageNum);

    MediaModel findById(String id);

    void save(MediaModel media);

    void delete(String id);
}
