package org.lpw.photon.ctrl.execute;

import org.lpw.photon.ctrl.template.Template;
import org.lpw.photon.ctrl.validate.Validate;

import java.lang.reflect.Method;

/**
 * 执行器。
 */
public interface Executor {
    /**
     * 获取执行器Bean实例。
     *
     * @return 执行器Bean实例。
     */
    Object getBean();

    /**
     * 获取执行方法。
     *
     * @return 执行方法。
     */
    Method getMethod();

    /**
     * 获取资源key。
     *
     * @return 资源key。
     */
    String getKey();

    /**
     * 获取授权。
     *
     * @return 授权。
     */
    String getPermit();

    /**
     * 获取验证规则集。
     *
     * @return 验证规则集。
     */
    Validate[] getValidates();

    /**
     * 获取输出模板实例。
     *
     * @return 输出模板实例。
     */
    Template getTemplate();

    /**
     * 获取模板文件名。
     *
     * @return 模板文件名。
     */
    String getView();
}
