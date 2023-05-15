package org.lpw.clivia.group.friend;

/**
 * 好友监听器。
 */
public interface FriendListener {
    /**
     * 好友申请。
     *
     * @param friend 好友。
     */
    void friendPropose(FriendModel friend);

    /**
     * 好友通过。
     *
     * @param friend 好友。
     */
    void friendPass(FriendModel friend);

    /**
     * 好友拒绝。
     *
     * @param friend 好友。
     */
    void friendReject(FriendModel friend);
}
