package org.lpw.photon.office.pptx;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import org.apache.poi.xslf.usermodel.XSLFTheme;
import org.lpw.photon.office.MediaWriter;

/**
 * 读上下文。
 */
public class ReaderContext {
    private MediaWriter mediaWriter;
    private XMLSlideShow xmlSlideShow;
    private XSLFTheme xslfTheme;
    private XSLFSlide xslfSlide;
    private boolean layout;

    ReaderContext(MediaWriter mediaWriter, XMLSlideShow xmlSlideShow) {
        this.mediaWriter = mediaWriter;
        this.xmlSlideShow = xmlSlideShow;
    }

    public MediaWriter getMediaWriter() {
        return mediaWriter;
    }

    public XMLSlideShow getXmlSlideShow() {
        return xmlSlideShow;
    }

    public XSLFTheme getTheme() {
        if (xslfTheme != null)
            return xslfTheme;

        for (POIXMLDocumentPart poixmlDocumentPart : xmlSlideShow.getRelations())
            if (poixmlDocumentPart instanceof XSLFTheme theme)
                return xslfTheme = theme;

        return null;
    }

    public XSLFSlide getXslfSlide() {
        return xslfSlide;
    }

    public void setXslfSlide(XSLFSlide xslfSlide) {
        this.xslfSlide = xslfSlide;
    }

    public boolean isLayout() {
        return layout;
    }

    public void setLayout(boolean layout) {
        this.layout = layout;
    }
}
