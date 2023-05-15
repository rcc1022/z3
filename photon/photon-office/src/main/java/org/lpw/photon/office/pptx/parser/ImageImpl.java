package org.lpw.photon.office.pptx.parser;

import com.alibaba.fastjson.JSONObject;
import org.apache.poi.sl.usermodel.PictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureData;
import org.apache.poi.xslf.usermodel.XSLFPictureShape;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.xslf.usermodel.XSLFSimpleShape;
import org.lpw.photon.office.MediaType;
import org.lpw.photon.office.OfficeHelper;
import org.lpw.photon.office.pptx.ReaderContext;
import org.lpw.photon.office.pptx.WriterContext;
import org.lpw.photon.util.Logger;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPicture;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Component("photon.office.pptx.parser.image")
public class ImageImpl implements Simple {
    @Inject
    private Logger logger;
    @Inject
    private OfficeHelper officeHelper;
    private final Set<String> contentTypes = Set.of("image/jpeg", "image/gif", "image/png");

    @Override
    public int getSort() {
        return 8;
    }

    @Override
    public void parseShape(ReaderContext readerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
        if (!(xslfSimpleShape instanceof XSLFPictureShape xslfPictureShape))
            return;

        parseClipping(xslfPictureShape, shape);
        JSONObject image = new JSONObject();
        XSLFPictureData xslfPictureData = xslfPictureShape.getPictureData();
        parseSize(xslfPictureData, image);
        image.put("contentType", xslfPictureData.getContentType());
        try {
            image.put("url", readerContext.getMediaWriter().write(MediaType.find(xslfPictureData.getContentType()),
                    xslfPictureData.getFileName(), xslfPictureData.getInputStream()));
        } catch (IOException e) {
            logger.warn(e, "获取PPTX图片数据时发生异常！");
        }
        image.put("state", ((CTPicture) xslfPictureShape.getXmlObject()).getBlipFill().getBlip().getCstate().toString());
        shape.put("image", image);
    }

    private void parseClipping(XSLFPictureShape xslfPictureShape, JSONObject shape) {
        Insets insets = xslfPictureShape.getClipping();
        if (insets == null)
            return;

        JSONObject clipping = new JSONObject();
        clipping.put("left", officeHelper.fromPercent(insets.left));
        clipping.put("top", officeHelper.fromPercent(insets.top));
        clipping.put("right", officeHelper.fromPercent(insets.right));
        clipping.put("bottom", officeHelper.fromPercent(insets.bottom));
        shape.put("clipping", clipping);
    }

    private void parseSize(XSLFPictureData xslfPictureData, JSONObject image) {
        Dimension dimension = xslfPictureData.getImageDimensionInPixels();
        JSONObject size = new JSONObject();
        size.put("width", dimension.width);
        size.put("height", dimension.height);
        image.put("size", size);
    }

    @Override
    public XSLFShape createShape(WriterContext writerContext, JSONObject shape) {
        if (!shape.containsKey("image"))
            return null;

        JSONObject image = shape.getJSONObject("image");
        try {
            InputStream inputStream = writerContext.getMediaReader().read(image);
            XSLFPictureData xslfPictureData = writerContext.getXmlSlideShow().addPicture(inputStream,
                    getPictureType(image.getString("url"), image.getString("contentType")));
            inputStream.close();

            return writerContext.getXslfSlide().createPicture(xslfPictureData);
        } catch (Throwable throwable) {
            logger.warn(throwable, "获取图片资源[{}]时发生异常！", image);

            return null;
        }
    }

    private PictureData.PictureType getPictureType(String url, String contentType) {
        if (!contentTypes.contains(contentType))
            logger.warn(null, "未处理图片类型[{}:{}]！", url, contentType);

        return switch (contentType) {
            case "image/jpeg" -> PictureData.PictureType.JPEG;
            case "image/gif" -> PictureData.PictureType.GIF;
            default -> PictureData.PictureType.PNG;
        };
    }

    @Override
    public void parseShape(WriterContext writerContext, XSLFSimpleShape xslfSimpleShape, JSONObject shape) {
    }
}
