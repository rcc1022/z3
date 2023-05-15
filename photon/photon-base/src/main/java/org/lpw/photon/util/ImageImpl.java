package org.lpw.photon.util;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Component("photon.util.image")
public class ImageImpl implements Image {
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private Logger logger;

    @Override
    public BufferedImage read(byte[] bytes) throws IOException {
        return read(new ByteArrayInputStream(bytes));
    }

    @Override
    public BufferedImage read(InputStream inputStream) throws IOException {
        BufferedImage image = ImageIO.read(inputStream);
        inputStream.close();

        return image;
    }

    @Override
    public BufferedImage thumbnail(BufferedImage image, int maxWidth, int maxHeight) {
        if (image == null || maxWidth <= 0 || maxHeight <= 0)
            return image;

        int width = image.getWidth();
        int height = image.getHeight();
        if (width > maxWidth) {
            height = height * maxWidth / width;
            width = maxWidth;
        }
        if (height > maxHeight) {
            width = width * maxHeight / height;
            height = maxHeight;
        }
        if (width <= 0 || height <= 0)
            return image;

        BufferedImage thumbnail = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = thumbnail.getGraphics();
        graphics.drawImage(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                0, 0, width, height, null);
        graphics.dispose();

        return thumbnail;
    }

    @Override
    public BufferedImage subimage(BufferedImage image, int x, int y, int width, int height) {
        if (x < 0 || y < 0 || width <= 0 || height <= 0)
            return image;

        int w = image.getWidth();
        int h = image.getHeight();
        if (x >= w || y >= h || (x == 0 && y == 0 && width >= w && height >= h))
            return image;

        return image.getSubimage(x, y, Math.min(width, w - x), Math.min(height, h - y));
    }

    @Override
    public BufferedImage scale(BufferedImage image, double scale) {
        return image == null || scale <= 0 || scale == 1.0D ? image :
                scale(image, numeric.toInt(image.getWidth() * scale), numeric.toInt(image.getHeight() * scale));
    }

    @Override
    public BufferedImage scale(BufferedImage image, int width, int height) {
        if (image == null || width <= 0 || height <= 0)
            return image;

        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH),
                0, 0, width, height, null);
        graphics.dispose();

        return bufferedImage;
    }

    @Override
    public byte[] write(BufferedImage image, Format format) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        write(image, format, outputStream);

        return outputStream.toByteArray();
    }

    @Override
    public void write(BufferedImage image, Format format, OutputStream outputStream) throws IOException {
        ImageIO.write(image, toString(format), outputStream);
        outputStream.close();
    }

    private String toString(Format format) {
        return switch (format) {
            case Jpeg -> "JPEG";
            case Gif -> "GIF";
            default -> "PNG";
        };
    }

    @Override
    public boolean svg2png(String svg, int width, int height, OutputStream outputStream) {
        try (Reader reader = new StringReader(svg)) {
            PNGTranscoder pngTranscoder = new PNGTranscoder();
            pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, 1.0F * width);
            pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, 1.0F * height);
            pngTranscoder.transcode(new TranscoderInput(reader), new TranscoderOutput(outputStream));
            outputStream.flush();

            return true;
        } catch (Throwable throwable) {
            logger.warn(throwable, "转换SVG文档[{}:{}:{}]为PNG图片时发生异常！", svg, width, height);

            return false;
        }
    }

    @Override
    public Format formatFromContentType(String contentType) {
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> Format.Jpeg;
            case "image/gif" -> Format.Gif;
            default -> Format.Png;
        };
    }

    @Override
    public boolean is(String contentType, String name) {
        int indexOf;
        if (validator.isEmpty(contentType) || validator.isEmpty(name) || !contentType.startsWith("image/")
                || (indexOf = name.lastIndexOf('.')) == -1)
            return false;

        String suffix = name.substring(indexOf).toLowerCase();
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> suffix.equals(".jpg") || suffix.equals(".jpeg");
            case "image/png" -> suffix.equals(".png");
            case "image/gif" -> suffix.equals(".gif");
            case "image/svg+xml" -> suffix.equals(".svg") || suffix.equals(".svg+xml");
            default -> false;
        };
    }

    @Override
    public int[] size(File file) {
        try {
            BufferedImage image = ImageIO.read(file);

            return new int[]{image.getWidth(), image.getHeight()};
        } catch (Exception e) {
            logger.warn(e, "读取图片文件[{}]宽高时发生异常！", file.getAbsolutePath());

            return null;
        }
    }

    @Override
    public int[] size(InputStream inputStream) {
        try {
            BufferedImage image = ImageIO.read(inputStream);

            return new int[]{image.getWidth(), image.getHeight()};
        } catch (Exception e) {
            logger.warn(e, "读取图片流宽高时发生异常！");

            return null;
        }
    }
}
