package org.lpw.photon.pdf;

import java.io.OutputStream;

import com.alibaba.fastjson.JSONObject;

public interface PdfWriter {
    /**
     * 输出PDF。
     * 
     * @param object       数据。
     * @param outputStream PDF输出流。
     */
    void write(JSONObject object, OutputStream outputStream);
}
