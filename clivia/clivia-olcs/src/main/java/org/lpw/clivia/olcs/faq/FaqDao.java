package org.lpw.clivia.olcs.faq;

import org.lpw.photon.dao.orm.PageList;

interface FaqDao {
    PageList<FaqModel> query(String subject, String content, int frequently, int pageSize, int pageNum);

    PageList<FaqModel> query(int frequently);

    FaqModel findById(String id);

    FaqModel findBySubject(String subject);

    void save(FaqModel faq);

    void delete(String id);
}