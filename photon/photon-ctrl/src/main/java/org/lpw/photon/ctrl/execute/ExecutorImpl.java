package org.lpw.photon.ctrl.execute;

import org.lpw.photon.ctrl.template.Template;
import org.lpw.photon.ctrl.validate.Validate;

import java.lang.reflect.Method;

public class ExecutorImpl implements Executor {
    private final Object bean;
    private final Method method;
    private final String key;
    private final String permit;
    private final Validate[] validates;
    private final Template template;
    private final String view;

    public ExecutorImpl(Object bean, Method method, String key, String permit, Validate[] validates, Template template, String view) {
        this.bean = bean;
        this.method = method;
        this.key = key;
        this.permit = permit;
        this.validates = validates;
        this.template = template;
        this.view = view;
    }

    @Override
    public Object getBean() {
        return bean;
    }

    @Override
    public Method getMethod() {
        return method;
    }

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public String getPermit() {
        return permit;
    }

    @Override
    public Validate[] getValidates() {
        return validates;
    }

    @Override
    public Template getTemplate() {
        return template;
    }

    @Override
    public String getView() {
        return view;
    }
}
