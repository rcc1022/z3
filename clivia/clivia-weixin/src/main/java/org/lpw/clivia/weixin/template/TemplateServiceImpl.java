package org.lpw.clivia.weixin.template;

import com.alibaba.fastjson.JSONObject;
import org.lpw.clivia.page.Pagination;
import org.lpw.clivia.weixin.WeixinService;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service(TemplateModel.NAME + ".service")
public class TemplateServiceImpl implements TemplateService {
    @Inject
    private Validator validator;
    @Inject
    private Pagination pagination;
    @Inject
    private WeixinService weixinService;
    @Inject
    private TemplateDao templateDao;

    @Override
    public JSONObject query(String key, String weixinKey, int type, String templateId, int state) {
        return templateDao.query(key, weixinKey, type, templateId, state, pagination.getPageSize(20), pagination.getPageNum()).toJson();
    }

    @Override
    public void save(TemplateModel template) {
        TemplateModel model = templateDao.find(template.getKey());
        template.setId(model == null ? null : model.getId());
        templateDao.save(template);
    }

    @Override
    public JSONObject send(String key, String receiver, String formId, JSONObject data, JSONObject args) {
        TemplateModel template = templateDao.find(key);
        if (template == null || template.getState() == 0)
            return new JSONObject();

        switch (template.getType()) {
            case 0:
                return weixinService.sendTemplateMessage(template.getWeixinKey(), receiver, template.getTemplateId(),
                        getUrl(template.getUrl(), args), template.getMiniAppId(), getUrl(template.getPage(), args), data, template.getColor());
            case 1:
                return weixinService.sendMiniTemplateMessage(template.getWeixinKey(), receiver, template.getTemplateId(),
                        getUrl(template.getPage(), args), formId, data, template.getKeyword());
            default:
                return new JSONObject();
        }
    }

    private String getUrl(String url, JSONObject object) {
        if (validator.isEmpty(object))
            return url;

        for (String key : object.keySet())
            url = url.replaceAll(key, object.getString(key));

        return url;
    }

    @Override
    public void delete(String id) {
        templateDao.delete(id);
    }
}
