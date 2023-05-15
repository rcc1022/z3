package org.lpw.clivia.faq;

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
    public JSONObject query(String key) {
        return faqDao.query(key, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public JSONObject get(String id) {
        FaqModel faq = faqDao.findById(id);

        return faq == null ? new JSONObject() : modelHelper.toJson(faq);
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
