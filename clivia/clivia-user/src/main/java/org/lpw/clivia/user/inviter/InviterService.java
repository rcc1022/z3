package org.lpw.clivia.user.inviter;

public interface InviterService {
    /**
     * 设置邀请码。
     *
     * @param code 邀请码。
     */
    void set(String code);

    /**
     * 获取邀请码。
     *
     * @return 邀请码。
     */
    String get();
}
