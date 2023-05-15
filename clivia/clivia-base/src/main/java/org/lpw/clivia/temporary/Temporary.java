package org.lpw.clivia.temporary;

import java.io.InputStream;

/**
 * 临时文件。
 *
 */
public interface Temporary {
    /**
     * 获取根目录。
     *
     * @return 根目录。
     */
    String root();

    /**
     * 获取保存路径。
     *
     * @param suffix 文件后缀。
     * @return 文件路径。
     */
    String newSavePath(String suffix);

    /**
     * 保存数据。
     *
     * @param bytes  数据。
     * @param suffix 文件后缀。
     * @return 文件相对路径。
     */
    String save(byte[] bytes, String suffix);

    /**
     * 保存流数据。
     *
     * @param inputStream 输入流。
     * @param suffix      文件后缀。
     * @return 文件相对路径。
     */
    String save(InputStream inputStream, String suffix);
}
