package org.lpw.photon.ctrl.context.local;

import org.lpw.photon.bean.BeanFactory;
import org.lpw.photon.ctrl.context.SessionAdapter;
import org.lpw.photon.util.Generator;

/**
 * 基于字符串的Session适配器实现。
 */
public class LocalSessionAdapter implements SessionAdapter {
    protected String id;

    public LocalSessionAdapter(String id) {
        this.id = id == null ? BeanFactory.getBean(Generator.class).random(32) : id;
    }

    @Override
    public String getId() {
        return id;
    }
}
