package org.lpw.clivia.weixin.template;

import org.lpw.photon.dao.orm.PageList;

interface TemplateDao {
    PageList<TemplateModel> query(String key, String weixinKey, int type, String templateId, int state, int pageSize, int pageNum);

    TemplateModel find(String key);

    void save(TemplateModel template);

    void delete(String id);
}
