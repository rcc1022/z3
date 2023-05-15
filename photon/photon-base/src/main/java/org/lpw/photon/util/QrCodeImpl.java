package org.lpw.photon.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Component("photon.util.qr-code")
public class QrCodeImpl implements QrCode {
    private static final int WHITE = Color.WHITE.getRGB();
    private static final int BLACK = Color.BLACK.getRGB();

    @Inject
    private Context context;
    @Inject
    private Io io;
    @Inject
    private Codec codec;
    @Inject
    private Logger logger;
    private final QRCodeWriter writer = new QRCodeWriter();
    private final QRCodeReader reader = new QRCodeReader();
    private final Map<DecodeHintType, ?> hints = Map.of(DecodeHintType.PURE_BARCODE, Boolean.TRUE);

    @Override
    public void create(String content, int size, String logo, String path) {
        try {
            create(content, size, logo, new FileOutputStream(path));
        } catch (Throwable e) {
            logger.warn(e, "生成二维码图片[{}:{}:{}:{}]时发生异常！", content, size, logo, path);
        }
    }

    @Override
    public void create(String content, int size, String logo, OutputStream outputStream) {
        try {
            create(content, size, io.exists(logo) ? new FileInputStream(logo) : null, outputStream);
        } catch (Throwable e) {
            logger.warn(e, "生成二维码图片[{}:{}:{}]时发生异常！", content, size, logo);
        }
    }

    @Override
    public void create(String content, int size, InputStream logo, OutputStream outputStream) {
        try {
            BufferedImage image = create(content, size);
            if (logo != null)
                logo(image, logo, size);
            ImageIO.write(image, "PNG", outputStream);
            outputStream.close();
        } catch (Throwable e) {
            logger.warn(e, "生成二维码图片[{}:{}]时发生异常！", content, size);
        }
    }

    @Override
    public String create(String content, int size, String logo) {
        try {
            return create(content, size, io.exists(logo) ? new FileInputStream(logo) : null);
        } catch (Throwable e) {
            logger.warn(e, "生成二维码图片[{}:{}:{}]时发生异常！", content, size, logo);

            return null;
        }
    }

    @Override
    public String create(String content, int size, InputStream logo) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            create(content, size, logo, outputStream);
            outputStream.close();

            return codec.encodeBase64(outputStream.toByteArray());
        } catch (Throwable e) {
            logger.warn(e, "生成二维码图片[{}:{}]时发生异常！", content, size);

            return null;
        }
    }

    private BufferedImage create(String content, int size) throws WriterException {
        Map<EncodeHintType, Object> hint = new HashMap<>();
        hint.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hint.put(EncodeHintType.CHARACTER_SET, context.getCharset(null));
        hint.put(EncodeHintType.MARGIN, 1);
        BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size, hint);
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                image.setRGB(i, j, matrix.get(i, j) ? BLACK : WHITE);

        return image;
    }

    private void logo(BufferedImage image, InputStream logo, int size) throws IOException {
        Graphics2D graphics = image.createGraphics();
        int wh = size / 5;
        int xy = wh << 1;
        int arc = wh >> 1;
        graphics.drawImage(ImageIO.read(logo), xy, xy, wh, wh, null);
        stroke(graphics, 4, xy - 3, wh + 6, arc, Color.WHITE);
        stroke(graphics, 2, xy - 1, wh + 2, arc, Color.GRAY);
        graphics.dispose();
        image.flush();
        logo.close();
    }

    private void stroke(Graphics2D graphics, int width, int xy, int wh, int arc, Color color) {
        BasicStroke stroke = new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        graphics.setStroke(stroke);
        RoundRectangle2D.Float round = new RoundRectangle2D.Float(xy, xy, wh, wh, arc, arc);
        graphics.setColor(color);
        graphics.draw(round);
    }

    @Override
    public String read(String path) {
        try (InputStream inputStream = new FileInputStream(path)) {
            return read(inputStream);
        } catch (Throwable e) {
            logger.warn(e, "读取二维码图片[{}]内容时发生异常！", path);

            return null;
        }
    }

    @Override
    public String readBase64(String base64) {
        try (InputStream inputStream = new ByteArrayInputStream(codec.decodeBase64(base64))) {
            return read(inputStream);
        } catch (Throwable e) {
            logger.warn(e, "读取二维码图片[{}]内容时发生异常！", base64);

            return null;
        }
    }

    @Override
    public String read(InputStream inputStream) {
        try {
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(inputStream))));
            inputStream.close();
            Result result = read(bitmap, true);
            if (result == null)
                result = read(bitmap, false);

            return result == null ? null : result.getText();
        } catch (Throwable e) {
            logger.warn(e, "读取二维码图片内容时发生异常！");

            return null;
        }
    }

    private Result read(BinaryBitmap bitmap, boolean pure) {
        try {
            if (pure)
                return reader.decode(bitmap, hints);

            return reader.decode(bitmap);
        } catch (Throwable e) {
            logger.warn(e, "读取二维码图片内容时发生异常！");

            return null;

        }
    }
}
