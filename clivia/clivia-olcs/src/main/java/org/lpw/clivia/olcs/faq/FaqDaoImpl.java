package org.lpw.clivia.olcs.faq;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(FaqModel.NAME + ".dao")
class FaqDaoImpl implements FaqDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<FaqModel> query(String subject, String content, int frequently, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder()
                .where("c_frequently", DaoOperation.Equals, frequently)
                .like(null, "c_subject", subject)
                .like(null, "c_content", content)
                .order("c_sort")
                .query(FaqModel.class, pageSize, pageNum);
    }

    @Override
    public PageList<FaqModel> query(int frequently) {
        return liteOrm.query(new LiteQuery(FaqModel.class).where("c_frequently=?").order("c_sort"), new Object[]{frequently});
    }

    @Override
    public FaqModel findById(String id) {
        return liteOrm.findById(FaqModel.class, id);
    }

    @Override
    public FaqModel findBySubject(String subject) {
        return liteOrm.findOne(new LiteQuery(FaqModel.class).where("c_subject=?").order("c_sort"), new Object[]{subject});
    }

    @Override
    public void save(FaqModel faq) {
        liteOrm.save(faq);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(FaqModel.class, id);
    }
}