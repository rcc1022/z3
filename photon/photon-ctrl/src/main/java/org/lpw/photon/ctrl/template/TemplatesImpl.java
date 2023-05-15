package org.lpw.photon.ctrl.template;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.bean.ContextRefreshedListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller("photon.ctrl.templates")
public class TemplatesImpl implements Templates, ContextRefreshedListener {
    @Value("${photon.ctrl.template.type:json}")
    private String type;
    private Map<String, Template> map;
    private final ThreadLocal<Boolean> nopack = new ThreadLocal<>();

    @Override
    public Template get() {
        return get(type);
    }

    @Override
    public Template get(String type) {
        return map.get(type);
    }

    @Override
    public void setNopack(boolean bool) {
        nopack.set(bool);
    }

    @Override
    public boolean isNopack() {
        Boolean bool = nopack.get();

        return bool != null && bool;
    }

    @Override
    public int getContextRefreshedSort() {
        return 6;
    }

    @Override
    public void onContextRefreshed() {
        if (map != null)
            return;

        map = new HashMap<>();
        BeanFactory.getBeans(Template.class).forEach(template -> map.put(template.getType(), template));
        map.put("", map.get(type));
    }
}
