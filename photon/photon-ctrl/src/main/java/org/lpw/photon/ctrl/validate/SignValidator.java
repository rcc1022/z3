package org.lpw.photon.ctrl.validate;

public interface SignValidator {
    /**
     * 设置是否验证签名。
     * 仅当前线程有效。
     *
     * @param enable true表示验证；false表示不验证。
     */
    void setSignEnable(boolean enable);
}
