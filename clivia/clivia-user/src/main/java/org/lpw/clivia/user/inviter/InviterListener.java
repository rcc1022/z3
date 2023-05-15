package org.lpw.clivia.user.inviter;

public interface InviterListener {
    /**
     * 新推广用户进入。
     *
     * @param user 推广人ID。
     */
    void invite(String user);
}
