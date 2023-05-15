package org.lpw.clivia.async;

/**
 * 通知器。
 *
 */
public interface AsyncNotifier {
    /**
     * 执行成功。
     *
     * @param result 执行结果。
     */
    void success(String result);

    /**
     * 执行失败。
     */
    void failure();
}
