package org.lpw.photon.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.font.FontProvider;

import org.lpw.photon.util.Context;
import org.lpw.photon.util.Logger;
import org.lpw.photon.util.Validator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("photon.pdf.converter")
public class PdfConverterImpl implements PdfConverter {
    @Inject
    private Validator validator;
    @Inject
    private Context context;
    @Inject
    private Logger logger;
    @Value("${photon.pdf.fonts:}")
    private String fonts;
    private ConverterProperties properties;

    @Override
    public boolean html2pdf(String html, OutputStream outputStream) {
        try {
            if (properties == null) {
                properties = new ConverterProperties();
                fonts();
            }

            PdfDocument document = new PdfDocument(new PdfWriter(outputStream));
            document.setDefaultPageSize(PageSize.A4);
            HtmlConverter.convertToPdf(html, document, properties);
            document.close();
            outputStream.close();

            return true;
        } catch (Throwable throwable) {
            logger.warn(throwable, "HTML转化为PDF时发生异常！");

            return false;
        }
    }

    private void fonts() {
        if (validator.isEmpty(fonts))
            return;

        FontProvider provider = new FontProvider();
        boolean has = false;
        String path = context.getAbsolutePath(fonts);
        for (File file : new File(path).listFiles()) {
            if (file.isFile())
                has = true;
            else if (file.isDirectory())
                provider.addDirectory(file.getAbsolutePath());
        }
        if (has)
            provider.addDirectory(path);
        properties.setFontProvider(provider);
    }

    @Override
    public boolean html2pdf(String html, String file) {
        try {
            return html2pdf(html, new FileOutputStream(file));
        } catch (Throwable throwable) {
            logger.warn(throwable, "HTML转化为PDF时发生异常！");

            return false;
        }
    }
}
