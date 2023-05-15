package org.lpw.photon.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * SSH客户端。
 */
public interface Ssh {
    /**
     * 执行指令集。
     *
     * @param host     主机。
     * @param port     端口号。
     * @param user     用户。
     * @param password 密码。
     * @param commands 指令集。
     * @return 执行结果；如果执行失败则返回null。
     */
    String shell(String host, int port, String user, String password, String... commands);

    /**
     * 下载文件。
     *
     * @param host         主机。
     * @param port         端口号。
     * @param user         用户。
     * @param password     密码。
     * @param path         文件路径。
     * @param outputStream 输出流。
     * @return 获取成功则返回true；否则返回false。
     */
    boolean get(String host, int port, String user, String password, String path, OutputStream outputStream);

    /**
     * 上传文件。
     *
     * @param host        主机。
     * @param port        端口号。
     * @param user        用户。
     * @param password    密码。
     * @param path        文件路径。
     * @param mode        文件模式。
     * @param inputStream 输入流。
     * @return 获取成功则返回true；否则返回false。
     */
    boolean put(String host, int port, String user, String password, String path, int mode, InputStream inputStream);
}
