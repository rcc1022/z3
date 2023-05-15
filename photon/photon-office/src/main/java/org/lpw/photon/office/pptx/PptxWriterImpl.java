package org.lpw.photon.office.pptx;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.photon.office.MediaReader;
import org.lpw.photon.office.OfficeHelper;
import org.lpw.photon.office.pptx.parser.Parser;
import org.lpw.photon.util.DateTime;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

@Component("photon.office.pptx.writer")
public class PptxWriterImpl implements PptxWriter {
    @Inject
    private DateTime dateTime;
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;
    @Inject
    private OfficeHelper officeHelper;
    @Inject
    private Parser parser;

    @Override
    public void write(OutputStream outputStream, JSONObject object, MediaReader mediaReader) {
        XMLSlideShow xmlSlideShow = newXMLSlideShow();
        parseSize(xmlSlideShow, object.getJSONObject("size"));
        parseSildes(new WriterContext(mediaReader, xmlSlideShow), object.getJSONArray("slides"));

        try {
            xmlSlideShow.write(outputStream);
        } catch (IOException e) {
            logger.warn(e, "输出PPTx到流时发生异常！");
        }
    }

    private XMLSlideShow newXMLSlideShow() {
        XMLSlideShow xmlSlideShow = new XMLSlideShow();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, -1 * (calendar.get(Calendar.ZONE_OFFSET) + calendar.get(Calendar.DST_OFFSET)));
        String time = dateTime.toString(calendar.getTime(), "yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            xmlSlideShow.getProperties().getCoreProperties().setCreated(time);
            xmlSlideShow.getProperties().getCoreProperties().setModified(time);
        } catch (InvalidFormatException e) {
            logger.warn(e, "设置PPT创建/修改时间时发生异常！");
        }

        return xmlSlideShow;
    }

    private void parseSize(XMLSlideShow xmlSlideShow, JSONObject size) {
        if (validator.isEmpty(size))
            return;

        xmlSlideShow.setPageSize(new Dimension(numeric.toInt(officeHelper.pixelToPoint(size.getIntValue("width"))),
                numeric.toInt(officeHelper.pixelToPoint(size.getIntValue("height")))));
    }

    private void parseSildes(WriterContext writerContext, JSONArray slides) {
        if (validator.isEmpty(slides))
            return;

        for (int i = 0, size = slides.size(); i < size; i++) {
            JSONObject slide = slides.getJSONObject(i);
            writerContext.setXslfSlide(writerContext.getXmlSlideShow().createSlide());
            parseBackground(writerContext, slide);
            parseShapes(writerContext, slide.getJSONArray("shapes"));
        }
    }

    private void parseBackground(WriterContext writerContext, JSONObject slide) {
        if (!slide.containsKey("background"))
            return;

        writerContext.getXslfSlide().getXmlObject().getCSld().addNewBg();
        parser.parseShape(writerContext, writerContext.getXslfSlide().getBackground(), slide.getJSONObject("background"));
    }

    private void parseShapes(WriterContext writerContext, JSONArray shapes) {
        for (int i = 0, size = shapes.size(); i < size; i++) {
            JSONObject shape = shapes.getJSONObject(i);
            XSLFShape xslfShape = parser.createShape(writerContext, shape);
            if (xslfShape == null) {
                logger.warn(null, "无法处理PPTx形状[{}]！", shape);

                continue;
            }

            if (xslfShape instanceof XSLFSimpleShape xslfSimpleShape)
                parser.parseShape(writerContext, xslfSimpleShape, shape);
        }
    }
}
