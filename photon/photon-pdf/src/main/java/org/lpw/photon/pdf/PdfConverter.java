package org.lpw.photon.pdf;

import java.io.OutputStream;

public interface PdfConverter {
    /**
     * HTML转化为PDF。
     * 
     * @param html         HTML文本。
     * @param outputStream PDF输出流。
     * @return 如果转化成功则返回true；否则返回false。
     */
    boolean html2pdf(String html, OutputStream outputStream);

    /**
     * HTML转化为PDF。
     * 
     * @param html HTML文本。
     * @param file PDF输出文件路径。
     * @return 如果转化成功则返回true；否则返回false。
     */
    boolean html2pdf(String html, String file);
}
