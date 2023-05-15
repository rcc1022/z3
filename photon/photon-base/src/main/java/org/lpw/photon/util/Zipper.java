package org.lpw.photon.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * 压缩/解压缩工具。
 */
public interface Zipper {
    /**
     * 打包压缩文件集。
     *
     * @param files        待压缩文件集。
     * @param outputStream 输出文件流。
     * @throws IOException IO异常。
     */
    void zip(List<File> files, OutputStream outputStream) throws IOException;

    /**
     * 打包压缩文件集。
     *
     * @param files        待压缩文件集，key为输出文件名，value为文件。
     * @param outputStream 输出文件流。
     * @throws IOException IO异常。
     */
    void zip(Map<String, File> files, OutputStream outputStream) throws IOException;

    /**
     * 打包压缩文件/目录。
     *
     * @param file         待压缩文件/目录。
     * @param outputStream 输出文件流。
     * @throws IOException IO异常。
     */
    void zip(File file, OutputStream outputStream) throws IOException;

    /**
     * 解压缩。
     *
     * @param input   压缩文件。
     * @param charset 编码。
     * @param output  输出目录。
     * @throws IOException IO异常。
     */
    void unzip(File input, Charset charset, File output) throws IOException;

    /**
     * 解压缩。
     *
     * @param inputStream 压缩输入流。
     * @param charset     编码。
     * @param output      输出目录。
     * @throws IOException IO异常。
     */
    void unzip(InputStream inputStream, Charset charset, File output) throws IOException;
}
