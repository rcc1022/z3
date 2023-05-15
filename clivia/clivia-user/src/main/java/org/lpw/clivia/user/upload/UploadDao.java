package org.lpw.clivia.user.upload;

import org.lpw.photon.dao.orm.PageList;

interface UploadDao {
    PageList<UploadModel> query(String user, int pageSize, int pageNum);

    UploadModel findById(String id);

    void save(UploadModel upload);

    void delete(String id, String user);
}