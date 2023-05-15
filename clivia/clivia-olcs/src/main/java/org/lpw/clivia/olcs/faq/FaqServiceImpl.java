package org.lpw.clivia.olcs.faq;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.photon.dao.model.ModelHelper;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(FaqModel.NAME + ".service")
public class FaqServiceImpl implements FaqService {
    @Inject
    private Validator validator;
    @Inject
    private ModelHelper modelHelper;
    @Inject
    private Pagination pagination;
    @Inject
    private FaqDao faqDao;

    @Override
    public JSONObject query(String subject, String content, int frequently) {
        return faqDao.query(subject, content, frequently, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONArray frequently() {
        return modelHelper.toJson(faqDao.query(1).getList());
    }

    @Override
    public FaqModel find(String subject) {
        return faqDao.findBySubject(subject);
    }

    @Override
    public void save(FaqModel faq) {
        FaqModel model = validator.isId(faq.getId()) ? faqDao.findById(faq.getId()) : null;
        if (model == null)
            faq.setId(null);
        faqDao.save(faq);
    }

    @Override
    public void delete(String id) {
        faqDao.delete(id);
    }
}
