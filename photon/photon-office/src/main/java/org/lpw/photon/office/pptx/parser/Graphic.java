package org.lpw.photon.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFGraphicFrame;
import org.lpw.photon.office.pptx.ReaderContext;

/**
 * 图表形状解析器。
 */
public interface Graphic extends Creater {
    /**
     * 获取处理顺序。
     *
     * @return 处理顺序。
     */
    int getSort();

    /**
     * 解析形状。
     *
     * @param readerContext    读上下文。
     * @param xslfGraphicFrame 形状。
     * @param shape            解析数据。
     */
    void parseShape(ReaderContext readerContext, XSLFGraphicFrame xslfGraphicFrame, JSONObject shape);
}
