package org.lpw.photon.pdf;

import java.io.OutputStream;

import javax.inject.Inject;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.lpw.photon.util.Json;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Component;

@Component("photon.pdf.writer")
public class PdfWriterImpl implements PdfWriter {
    @Inject
    private Json json;
    @Inject
    private Logger logger;

    @Override
    public void write(JSONObject object, OutputStream outputStream) {
        try (PDDocument document = new PDDocument()) {
            if (object.containsKey("pages")) {
                JSONArray pages = object.getJSONArray("pages");
                for (int i = 0, size = pages.size(); i < size; i++) {
                    page(document, pages.getJSONObject(i));
                }
            }
            document.save(outputStream);
        } catch (Throwable throwable) {
            logger.warn(throwable, "输出PDF时发生异常！");
        }
    }

    private void page(PDDocument document, JSONObject object) throws Throwable {
        PDPage page = new PDPage();
        if (object.containsKey("elements")) {
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            JSONArray elements = object.getJSONArray("elements");
            for (int i = 0, size = elements.size(); i < size; i++) {
                JSONObject element = elements.getJSONObject(i);
                String type = element.getString("type");
                if (type.equals("text"))
                    text(contentStream, element);
            }
            contentStream.close();
        }
        document.addPage(page);
    }

    private void text(PDPageContentStream contentStream, JSONObject object) throws Throwable {
        contentStream.beginText();
        contentStream.newLineAtOffset(object.getFloatValue("x"), object.getFloatValue("y"));
        JSONArray texts = object.getJSONArray("texts");
        for (int i = 0, size = texts.size(); i < size; i++) {
            JSONObject text = texts.getJSONObject(i);
            contentStream.setFont(font(text), object.getFloatValue("size"));
            contentStream.showText(text.getString("text"));
        }
        contentStream.endText();
    }

    private PDFont font(JSONObject object) {
        boolean bold = json.hasTrue(object, "bold");
        boolean italic = json.hasTrue(object, "italic");
        if (bold && italic)
            return PDType1Font.TIMES_BOLD_ITALIC;

        if (bold)
            return PDType1Font.TIMES_BOLD;

        if (italic)
            return PDType1Font.TIMES_ITALIC;

        return PDType1Font.TIMES_ROMAN;
    }
}
