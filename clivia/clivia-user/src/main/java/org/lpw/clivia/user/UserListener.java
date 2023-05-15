package org.lpw.clivia.user;

/**
 * 用户监听器。
 */
public interface UserListener {
    /**
     * 同步用户。
     *
     * @param user 用户。
     */
    default void userSync(UserModel user) {
    }

    /**
     * 新用户注册。
     *
     * @param user 用户。
     */
    default void userSignUp(UserModel user) {
    }

    /**
     * 用户登入。
     *
     * @param user 用户。
     */
    default void userSignIn(UserModel user) {
    }

    /**
     * 用户等出。
     *
     * @param user 用户。
     */
    default void userSignOut(UserModel user) {
    }

    /**
     * 用户删除。
     *
     * @param user 用户。
     */
    default void userDelete(UserModel user) {
    }
}
