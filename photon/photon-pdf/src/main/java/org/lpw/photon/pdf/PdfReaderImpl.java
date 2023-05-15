package org.lpw.photon.pdf;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.lpw.photon.pdf.parser.GraphicsParser;
import org.lpw.photon.pdf.parser.TextParser;
import org.lpw.photon.util.Logger;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component("photon.pdf.reader")
public class PdfReaderImpl implements PdfReader {
    @Inject
    private Logger logger;
    @Inject
    private PdfHelper pdfHelper;

    @Override
    public JSONObject read(InputStream inputStream, MediaWriter mediaWriter) {
        JSONObject object = new JSONObject();
        JSONArray pages = new JSONArray();
        int pageHeight = 0;
        try (PDDocument pdDocument = Loader.loadPDF(inputStream)) {
            int size = pdDocument.getNumberOfPages();
            if (size == 0)
                return object;

            for (int i = 0; i < size; i++) {
                PDPage pdPage = pdDocument.getPage(i);
                if (i == 0) {
                    parseSize(object, pdPage);
                    pageHeight = object.getJSONObject("size").getIntValue("height");
                }

                JSONArray elements = new JSONArray();
                parseImage(elements, pdPage, mediaWriter);
                parseText(elements, pdDocument, pageHeight, i);

                JSONObject page = new JSONObject();
                page.put("elements", elements);
                pages.add(page);
            }
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "解析PDF数据时发生异常！");
        }

        object.put("pages", pages);

        return object;
    }

    private void parseSize(JSONObject object, PDPage pdPage) {
        JSONObject size = new JSONObject();
        size.put("width", pdfHelper.pointToPixel(pdPage.getCropBox().getWidth()));
        size.put("height", pdfHelper.pointToPixel(pdPage.getCropBox().getHeight()));
        object.put("size", size);
    }

    private void parseImage(JSONArray elements, PDPage pdPage, MediaWriter mediaWriter) throws IOException {
        GraphicsParser graphicsParser = new GraphicsParser(pdPage, pdfHelper, mediaWriter);
        graphicsParser.processPage(pdPage);
        merge(elements, graphicsParser.getArray());
    }

    private void parseText(JSONArray elements, PDDocument pdDocument, int pageHeight, int page) throws IOException {
        TextParser textParser = new TextParser(pdfHelper, pageHeight);
        textParser.setStartPage(page + 1);
        textParser.setEndPage(page + 1);
        textParser.getText(pdDocument);
        merge(elements, textParser.getArray());
    }

    private void merge(JSONArray elements, JSONArray array) {
        if (!array.isEmpty())
            elements.addAll(array);
    }

    @Override
    public String png(InputStream inputStream, MediaWriter mediaWriter, int page, float scale, int width) {
        return image(inputStream, mediaWriter, MediaType.Png, scale, width, true, page);
    }

    @Override
    public List<String> pngs(InputStream inputStream, MediaWriter mediaWriter, float scale, int width, boolean merge) {
        return images(inputStream, mediaWriter, MediaType.Png, scale, width, true, merge);
    }

    @Override
    public String jpeg(InputStream inputStream, MediaWriter mediaWriter, int page, float scale, int width) {
        return image(inputStream, mediaWriter, MediaType.Jpeg, scale, width, false, page);
    }

    @Override
    public List<String> jpegs(InputStream inputStream, MediaWriter mediaWriter, float scale, int width, boolean merge) {
        return images(inputStream, mediaWriter, MediaType.Jpeg, scale, width, false, merge);
    }

    private String image(InputStream inputStream, MediaWriter mediaWriter, MediaType mediaType, float scale, int width, boolean argb, int page) {
        if (scale <= 0.0f && width <= 0)
            scale = 1.0f;
        try (PDDocument document = Loader.loadPDF(inputStream)) {
            if (width > 0)
                scale = width / document.getPage(page).getCropBox().getWidth();

            return write(mediaWriter, mediaType, new PDFRenderer(document).renderImage(page, scale,
                    argb ? ImageType.ARGB : ImageType.RGB), page + mediaType.getSuffix());
        } catch (IOException e) {
            logger.warn(e, "读取PDF为图片时发生异常！");

            return null;
        }
    }

    private List<String> images(InputStream inputStream, MediaWriter mediaWriter, MediaType mediaType,
                                float scale, int width, boolean argb, boolean merge) {
        if (scale <= 0.0f && width <= 0)
            scale = 1.0f;
        List<String> list = new ArrayList<>();
        try (PDDocument document = Loader.loadPDF(inputStream)) {
            PDFRenderer renderer = new PDFRenderer(document);
            BufferedImage together = null;
            for (int i = 0, size = document.getNumberOfPages(); i < size; i++) {
                if (width > 0)
                    scale = width / document.getPage(i).getCropBox().getWidth();
                BufferedImage bufferedImage = renderer.renderImage(i, scale, argb ? ImageType.ARGB : ImageType.RGB);
                list.add(write(mediaWriter, mediaType, bufferedImage, i + mediaType.getSuffix()));
                if (!merge)
                    continue;

                if (together == null)
                    together = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight() * size,
                            argb ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
                together.getGraphics().drawImage(bufferedImage, 0, i * bufferedImage.getHeight(), null);
            }

            if (merge && together != null)
                list.add(0, write(mediaWriter, mediaType, together, "together" + mediaType.getSuffix()));
            inputStream.close();
        } catch (IOException e) {
            logger.warn(e, "读取PDF为图片时发生异常！");
        }

        return list;
    }

    private String write(MediaWriter mediaWriter, MediaType mediaType, BufferedImage bufferedImage, String fileName) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, mediaType.getFormatName(), byteArrayOutputStream);
        byteArrayOutputStream.close();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        String url = mediaWriter.write(mediaType, fileName, byteArrayInputStream);
        byteArrayInputStream.close();

        return url;
    }
}
