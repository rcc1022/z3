package org.lpw.clivia.weixin.template;

import com.alibaba.fastjson.JSONObject;

public interface TemplateService {
    String VALIDATOR_EXISTS = TemplateModel.NAME + ".validator.exists";

    /**
     * 检索。
     *
     * @param key        引用key。
     * @param weixinKey  微信key。
     * @param type       类型：-1-全部；0-公众号；1-小程序。
     * @param templateId 模板ID。
     * @param state      状态：-1-全部；0-待审核；1-已上线。
     * @return 信息集。
     */
    JSONObject query(String key, String weixinKey, int type, String templateId, int state);

    /**
     * 保存。
     *
     * @param template 配置。
     */
    void save(TemplateModel template);

    /**
     * 发送。
     *
     * @param key      引用key。
     * @param receiver 接收者。
     * @param formId   表单ID。
     * @param data     参数集。
     * @param args     链接参数集。
     * @return 发送结果。
     */
    JSONObject send(String key, String receiver, String formId, JSONObject data, JSONObject args);

    /**
     * 删除。
     *
     * @param id ID值。
     */
    void delete(String id);
}
