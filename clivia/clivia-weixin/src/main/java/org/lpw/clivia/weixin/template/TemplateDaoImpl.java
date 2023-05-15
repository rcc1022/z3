package org.lpw.clivia.weixin.template;

import org.lpw.clivia.dao.DaoHelper;
import org.lpw.clivia.dao.DaoOperation;
import org.lpw.photon.dao.orm.PageList;
import org.lpw.photon.dao.orm.lite.LiteOrm;
import org.lpw.photon.dao.orm.lite.LiteQuery;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;

@Repository(TemplateModel.NAME + ".dao")
class TemplateDaoImpl implements TemplateDao {
    @Inject
    private LiteOrm liteOrm;
    @Inject
    private DaoHelper daoHelper;

    @Override
    public PageList<TemplateModel> query(String key, String weixinKey, int type, String templateId, int state, int pageSize, int pageNum) {
        return daoHelper.newQueryBuilder().where("c_key", DaoOperation.Equals, key)
                .where("c_weixin_key", DaoOperation.Equals, weixinKey)
                .where("c_type", DaoOperation.Equals, type)
                .where("c_template_id", DaoOperation.Equals, templateId)
                .where("c_state", DaoOperation.Equals, state)
                .order("c_state desc,c_type")
                .query(TemplateModel.class, pageSize, pageNum);
    }

    @Override
    public TemplateModel find(String key) {
        return liteOrm.findOne(new LiteQuery(TemplateModel.class).where("c_key=?"), new Object[]{key});
    }

    @Override
    public void save(TemplateModel template) {
        liteOrm.save(template);
    }

    @Override
    public void delete(String id) {
        liteOrm.deleteById(TemplateModel.class, id);
    }
}
