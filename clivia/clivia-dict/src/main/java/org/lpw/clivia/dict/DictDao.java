package org.lpw.clivia.dict;

import org.lpw.photon.dao.orm.PageList;

interface DictDao {
    PageList<DictModel> query(String key, int pageSize, int pageNum);

    DictModel findById(String id);

    DictModel findByKeyValue(String key, String value);

    void save(DictModel dict);

    void delete(String id);
}