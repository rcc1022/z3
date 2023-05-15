package org.lpw.photon.pdf;

import com.alibaba.fastjson.JSONObject;

import java.io.InputStream;
import java.util.List;

/**
 * PDF读取器。
 */
public interface PdfReader {
    /**
     * 读取并解析PDF数据。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @return JSON数据。
     */
    JSONObject read(InputStream inputStream, MediaWriter mediaWriter);

    /**
     * 转化为PNG图片。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @param page        转化页码，0表示第一页。
     * @param scale       缩放比例，小于等于0则默认为1。
     * @param width       宽度，大于零则重新计算缩放比例。
     * @return 图片URL地址。
     */
    String png(InputStream inputStream, MediaWriter mediaWriter, int page, float scale, int width);

    /**
     * 转化为PNG图片集。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @param scale       缩放比例，小于等于0则默认为1。
     * @param width       宽度，大于零则重新计算缩放比例。
     * @param merge       是否合并，如果是则将所有图片合并为一张，并保存为第一个元素。
     * @return 图片URL地址集，首张为各页合并图。
     */
    List<String> pngs(InputStream inputStream, MediaWriter mediaWriter, float scale, int width, boolean merge);

    /**
     * 转化为JPEG图片。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @param page        转化页码，0表示第一页。
     * @param scale       缩放比例，小于等于0则默认为1。
     * @param width       宽度，大于零则重新计算缩放比例。
     * @return 图片URL地址。
     */
    String jpeg(InputStream inputStream, MediaWriter mediaWriter, int page, float scale, int width);

    /**
     * 转化为JPEGS图片集。
     *
     * @param inputStream 输入流。
     * @param mediaWriter 媒体输出器。
     * @param scale       缩放比例，小于等于0则默认为1。
     * @param width       宽度，大于零则重新计算缩放比例。
     * @param merge       是否合并，如果是则将所有图片合并为一张，并保存为第一个元素。
     * @return 图片URL地址集，首张为各页合并图。
     */
    List<String> jpegs(InputStream inputStream, MediaWriter mediaWriter, float scale, int width, boolean merge);
}
