package org.lpw.clivia.keyvalue;

import org.lpw.photon.dao.orm.PageList;

interface KeyvalueDao {
    PageList<KeyvalueModel> query(int pageSize, int pageNum);

    PageList<KeyvalueModel> query(String key, int pageSize, int pageNum);

    KeyvalueModel findById(String id);

    KeyvalueModel findByKey(String key);

    void save(KeyvalueModel keyvalue);

    void delete(String id);
}
