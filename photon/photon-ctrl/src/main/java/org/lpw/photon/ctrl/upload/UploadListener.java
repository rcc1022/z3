package org.lpw.photon.ctrl.upload;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;

/**
 * 上传文件处理监听器。
 */
public interface UploadListener {
    /**
     * 获取监听器key；监听器key必须与上传字段名一致。
     *
     * @return 监听器key，支持正则表达式。
     */
    String getKey();

    /**
     * 获取文件Content-Type属性。
     *
     * @param uploadReader 上传数据读取器。
     * @return 文件Content-Type属性。
     */
    default String getContentType(UploadReader uploadReader) {
        return uploadReader.getContentType();
    }

    /**
     * 验证是否允许上传。
     *
     * @param uploadReader 上传数据读取器。
     * @return 如果允许则返回true；否则返回false。
     */
    boolean isUploadEnable(UploadReader uploadReader);

    /**
     * 处理数据。
     *
     * @param uploadReader 上传数据读取器。
     * @return 处理后的数据。如果返回为null则继续执行存储操作，否则不存储。
     * @throws IOException IO异常。
     */
    default JSONObject settle(UploadReader uploadReader) throws IOException {
        return null;
    }

    /**
     * 获取存储处理器。
     *
     * @return 存储处理器。
     */
    default String getStorage() {
        return null;
    }

    /**
     * 获取文件保存路径。
     *
     * @param uploadReader 上传数据读取器。
     * @return 文件保存路径。
     */
    default String getPath(UploadReader uploadReader) {
        return "";
    }

    /**
     * 获取文件保存后缀。
     *
     * @param uploadReader 上传数据读取器。
     * @return 文件后缀；null表示使用默认后缀。
     */
    default String getSuffix(UploadReader uploadReader) {
        return null;
    }

    /**
     * 获取图片大小。 当上传文件为图片时，并且返回的图片大小（长、高）大于0时，自动将图片修改为长宽不超过设置值的图片，并进行压缩。
     *
     * @return 图片大小[长, 高]或[百分比]，如果返回null则表示不需要调整图片。
     */
    default int[] getImageSize() {
        return null;
    }

    /**
     * 上传完成。
     *
     * @param uploadReader 上传数据读取器。
     * @param object       处理结果。
     */
    default void complete(UploadReader uploadReader, JSONObject object) {
    }

    /**
     * 获取上传成功消息。
     * 
     * @return 上传成功消息。
     */
    default String message() {
        return null;
    }
}
