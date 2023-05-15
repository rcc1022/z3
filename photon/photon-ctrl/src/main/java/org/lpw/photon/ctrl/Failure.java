package org.lpw.photon.ctrl;

public enum Failure {
    /**
     * 无权限。
     */
    NotPermit("photon.ctrl.not-permit"),
    /**
     * 安全隐患。
     */
    Danger("photon.ctrl.danger"),
    /**
     * 系统繁忙。
     */
    Busy("photon.ctrl.busy"),
    /**
     * 运行期异常。
     */
    Exception("photon.ctrl.exception");

    private String messageKey;

    Failure(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getMessageKey() {
        return messageKey;
    }
}
