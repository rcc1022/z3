package org.lpw.photon.pdf;

import com.alibaba.fastjson.JSONObject;
import org.lpw.photon.util.Numeric;
import org.lpw.photon.util.Validator;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.awt.Color;

@Component("photon.pdf.helper")
public class PdfHelperImpl implements PdfHelper {
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;

    @Override
    public boolean is(String contentType, String fileName) {
        if (!"application/pdf".equals(contentType) || fileName == null)
            return false;

        int indexOf = fileName.lastIndexOf('.');
        if (indexOf == -1)
            return false;

        return fileName.substring(indexOf).equalsIgnoreCase(".pdf");
    }

    @Override
    public int pointToPixel(double point) {
        return numeric.toInt(point * 96 / 72);
    }

    @Override
    public JSONObject toJsonColor(float[] fs) {
        JSONObject object = new JSONObject();
        if (validator.isEmpty(fs))
            putColor(object, 0.0F, 0.0F, 0.0F);
        else if (fs.length == 1)
            putColor(object, fs[0], fs[0], fs[0]);
        else if (fs.length == 3)
            putColor(object, fs[0], fs[1], fs[2]);

        return object;
    }

    private void putColor(JSONObject object, float red, float green, float blue) {
        object.put("red", numeric.toInt(red * 255));
        object.put("green", numeric.toInt(green * 255));
        object.put("blue", numeric.toInt(blue * 255));
    }

    @Override
    public Color toColor(float[] fs) {
        if (validator.isEmpty(fs))
            return toColor(0.0F, 0.0F, 0.0F);

        if (fs.length == 1)
            return toColor(fs[0], fs[0], fs[0]);

        return toColor(fs[0], fs[1], fs[2]);
    }

    private Color toColor(float red, float green, float blue) {
        return new Color(numeric.toInt(red * 255), numeric.toInt(green * 255), numeric.toInt(blue * 255));
    }
}
