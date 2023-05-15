package org.lpw.photon.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * IO操作工具集。
 */
public interface Io {
    /**
     * 判断路径是否存在。
     *
     * @param path 目录。
     * @return 如果存在则返回true；否则返回false。
     */
    boolean exists(File path);

    /**
     * 判断路径是否存在。
     *
     * @param path 目录。
     * @return 如果存在则返回true；否则返回false。
     */
    boolean exists(String path);

    /**
     * 创建目录（集）。
     *
     * @param file 目录。
     */
    void mkdirs(File file);

    /**
     * 创建目录（集）。
     *
     * @param path 目录。
     */
    void mkdirs(String path);

    /**
     * 读取指定路径下的文件。
     *
     * @param path 文件所在路径。
     * @return 文件内容；如果读取异常则返回null。
     */
    byte[] read(String path);

    /**
     * 读取指定路径下的文件。
     *
     * @param path 文件所在路径。
     * @return 文件内容；如果读取异常则返回null。
     */
    String readAsString(String path);

    /**
     * 读取输入流中的数据。
     *
     * @param inputStream 输入流。
     * @return 数据；如果读取异常则返回null。
     */
    byte[] read(InputStream inputStream);

    /**
     * 读取输入流中的数据。
     *
     * @param inputStream 输入流。
     * @return 数据；如果读取异常则返回null。
     */
    String readAsString(InputStream inputStream);

    /**
     * 写入文件。
     *
     * @param path  文件所在路径。
     * @param bytes 文件内容。
     */
    void write(String path, byte[] bytes);

    /**
     * 写入文件。
     *
     * @param path        文件所在路径。
     * @param inputStream 输入流。
     */
    void write(String path, InputStream inputStream);

    /**
     * 复制文件。
     *
     * @param input  输入文件路径。
     * @param output 输出文件路径。
     */
    void copy(String input, String output);

    /**
     * 将输入流中的数据复制到输出流中。
     *
     * @param inputStream  输入流。
     * @param outputStream 输出流。
     * @throws IOException 未处理IO读写异常。
     */
    void copy(InputStream inputStream, OutputStream outputStream) throws IOException;

    /**
     * 将字符串输出到输出流中。
     *
     * @param outputStream 输出流。
     * @param source       要输出的字符串。
     * @throws IOException 未处理IO读写异常。
     */
    void write(OutputStream outputStream, StringBuffer source) throws IOException;

    /**
     * 关闭输入流。
     *
     * @param inputStream 输入流。
     */
    void close(InputStream inputStream);

    /**
     * 关闭输出流。
     *
     * @param outputStream 输出流。
     */
    void close(OutputStream outputStream);

    /**
     * 移动文件。
     *
     * @param source 源文件。
     * @param target 目标文件。
     * @return 如果移动成功则返回目标文件；失败则返回源文件。
     */
    File move(File source, File target);

    /**
     * 移动文件。
     *
     * @param source 源文件。
     * @param target 目标文件。
     * @return 如果移动成功则返回目标文件；失败则返回源文件。
     */
    String move(String source, String target);

    /**
     * 删除文件/目录（递归）。
     *
     * @param file 文件/目录。
     */
    void delete(File file);

    /**
     * 删除文件/目录（递归）。
     *
     * @param path 文件/目录。
     */
    void delete(String path);
}
