package org.lpw.photon.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.photon.office.pptx.ReaderContext;
import org.lpw.photon.office.pptx.WriterContext;
import org.lpw.photon.util.Numeric;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("photon.office.pptx.parser.rotation")
public class RotationImpl implements Simple, Zero {
    @Inject
    private Numeric numeric;

    @Override
    public int getSort() {
        return 2;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (xslfSimpleShape.getRotation() == 0.0D)
            return;

        int rotation = numeric.toInt(xslfSimpleShape.getRotation());
        shape.put("rotation", rotation < 0 ? (360 + rotation) : rotation);
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (shape.containsKey("rotation"))
            xslfSimpleShape.setRotation(shape.getDoubleValue("rotation"));
    }

    @Override
    public void zero(XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (shape.containsKey("rotation"))
            xslfSimpleShape.setRotation(0.0D);
    }

    @Override
    public void reset(XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (!shape.containsKey("rotation"))
            return;

        int rotation = shape.getIntValue("rotation");
        if (rotation == 0)
            return;

        xslfSimpleShape.setRotation(rotation);
    }
}
