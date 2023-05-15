package org.lpw.clivia.faq;

import org.lpw.photon.dao.orm.PageList;

interface FaqDao {
    PageList<FaqModel> query(String key, int pageSize, int pageNum);

    FaqModel findById(String id);

    void save(FaqModel faq);

    void delete(String id);
}