package org.lpw.photon.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.apache.xmlbeans.XmlObject;
import org.lpw.photon.office.pptx.ReaderContext;
import org.lpw.photon.office.pptx.WriterContext;
import org.lpw.photon.util.Json;
import org.openxmlformats.schemas.drawingml.x2006.main.CTCamera;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScene3D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTSphereCoords;
import org.openxmlformats.schemas.presentationml.x2006.main.CTShape;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component("photon.office.pptx.parser.filp")
public class FlipImpl implements Simple, Zero {
    @Inject
    private Json json;

    @Override
    public int getSort() {
        return 1;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        JSONObject flip = new JSONObject();
        CTSphereCoords ctSphereCoords = getScene3D(xslfSimpleShape);
        if (xslfSimpleShape.getFlipHorizontal() || (ctSphereCoords != null && ctSphereCoords.getLat() == 10800000))
            flip.put("horizontal", true);
        if (xslfSimpleShape.getFlipVertical() || (ctSphereCoords != null && ctSphereCoords.getLon() == 10800000))
            flip.put("vertical", true);
        if (!flip.isEmpty())
            shape.put("flip", flip);
    }

    private CTSphereCoords getScene3D(XSLFSimpleShape xslfSimpleShape) {
        XmlObject xmlObject = xslfSimpleShape.getXmlObject();
        if (!(xmlObject instanceof CTShape ctShape))
            return null;

        CTScene3D ctScene3D = ctShape.getSpPr().getScene3D();
        if (ctScene3D == null)
            return null;

        CTCamera ctCamera = ctScene3D.getCamera();
        if (ctCamera == null)
            return null;

        return ctCamera.getRot();
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        return null;
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (!shape.containsKey("flip"))
            return;

        JSONObject flip = shape.getJSONObject("flip");
        if (json.hasTrue(flip, "horizontal"))
            xslfSimpleShape.setFlipHorizontal(true);
        if (json.hasTrue(flip, "vertical"))
            xslfSimpleShape.setFlipVertical(true);
    }

    @Override
    public void zero(XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (!shape.containsKey("flip"))
            return;

        JSONObject flip = shape.getJSONObject("flip");
        if (flip.containsKey("horizontal"))
            xslfSimpleShape.setFlipHorizontal(!xslfSimpleShape.getFlipHorizontal());
        if (flip.containsKey("vertical"))
            xslfSimpleShape.setFlipVertical(!xslfSimpleShape.getFlipVertical());
    }

    @Override
    public void reset(XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        zero(xslfSimpleShape, shape);
    }
}
