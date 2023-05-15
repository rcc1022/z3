package org.lpw.photon.office;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.util.Context;
import org.lpw.photon.util.Generator;
import org.lpw.photon.util.Numeric;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component("photon.office.helper")
public class OfficeHelperImpl implements OfficeHelper {
    @Inject
    private Generator generator;
    @Inject
    private Context context;
    @Inject
    private Numeric numeric;
    @Value("${photon.office.temp-path:}")
    private String temp;
    private final Set<String> excelContentTypes = new HashSet<>(Arrays.asList("application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    private final Set<String> excelSuffixes = new HashSet<>(Arrays.asList(".xls", ".xlsx"));
    private final Set<String> pptContentTypes = new HashSet<>(Arrays.asList("application/octet-stream",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation"));
    private final Set<String> pptSuffixes = new HashSet<>(Arrays.asList(".ppt", ".pptx"));
    private final Set<String> wordContentTypes = new HashSet<>(Arrays.asList("application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
    private final Set<String> wordSuffixes = new HashSet<>(Arrays.asList(".doc", ".docx"));

    @Override
    public boolean isExcel(String contentType, String fileName) {
        if (!excelContentTypes.contains(contentType) || fileName == null)
            return false;

        int indexOf = fileName.lastIndexOf('.');
        if (indexOf == -1)
            return false;

        return excelSuffixes.contains(fileName.substring(indexOf).toLowerCase());
    }

    @Override
    public boolean isPpt(String contentType, String fileName) {
        if (!pptContentTypes.contains(contentType) || fileName == null)
            return false;

        int indexOf = fileName.lastIndexOf('.');
        if (indexOf == -1)
            return false;

        return pptSuffixes.contains(fileName.substring(indexOf).toLowerCase());
    }

    @Override
    public boolean isWord(String contentType, String fileName) {
        if (!wordContentTypes.contains(contentType) || fileName == null)
            return false;

        int indexOf = fileName.lastIndexOf('.');
        if (indexOf == -1)
            return false;

        return wordSuffixes.contains(fileName.substring(indexOf).toLowerCase());
    }

    @Override
    public String getTempPath(String name) {
        return context.getAbsolutePath(temp + "/" + name + "/" + generator.random(32));
    }

    @Override
    public long pixelToEmu(int pixel) {
        return pixel * 9525L;
    }

    @Override
    public int emuToPixel(long emu) {
        return numeric.toInt(emu / 9525);
    }

    @Override
    public double emuToPoint(long emu) {
        return emu / 9525.0D * 72 / 96;
    }

    @Override
    public int pointToPixel(double point) {
        return numeric.toInt(point * 96 / 72);
    }

    @Override
    public double pixelToPoint(int pixel) {
        return pixel * 72 / 96.0D;
    }

    @Override
    public double fromPercent(int percent) {
        return percent / 100000.0D;
    }

    @Override
    public int toPercent(double value) {
        return numeric.toInt(value * 100000);
    }

    @Override
    public JSONObject colorToJson(Color color) {
        JSONObject object = new JSONObject();
        if (color == null)
            return object;

        object.put("red", color.getRed());
        object.put("green", color.getGreen());
        object.put("blue", color.getBlue());
        object.put("alpha", color.getAlpha());

        return object;
    }

    @Override
    public Color jsonToColor(JSONObject object) {
        return new Color(object.getIntValue("red"), object.getIntValue("green"), object.getIntValue("blue"),
                object.containsKey("alpha") ? object.getIntValue("alpha") : 255);
    }
}
