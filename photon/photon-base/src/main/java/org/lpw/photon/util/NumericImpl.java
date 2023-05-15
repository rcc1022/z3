package org.lpw.photon.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("photon.util.numeric")
public class NumericImpl implements Numeric {
    private final String integerPattern = "^[+-]?\\d+$";
    private final String floatPattern = "^[+-]?\\d+(\\.\\d+)?$";

    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    private final Map<String, DecimalFormat> formats = new ConcurrentHashMap<>();
    private final char[] chineses = {'零', '一', '二', '三', '四', '五', '六', '七', '八', '九', '十', '百', '千', '万', '亿'};
    private final char[] bigChineses = {'零', '壹', '贰', '叁', '肆', '伍', '陆', '柒', '捌', '玖', '拾', '佰', '仟', '萬', '億'};

    @Override
    public int toInt(Object object) {
        return toInt(object, 0);
    }

    @Override
    public int toInt(Object object, int defaultValue) {
        if (validator.isEmpty(object))
            return defaultValue;

        if (object instanceof Integer n)
            return n;

        if (object instanceof Long l)
            return (int) (long) l;

        if (object instanceof Float f)
            return Math.round(f);

        if (object instanceof Double d)
            return (int) Math.round(d);

        String string = toString(object);
        if (string.indexOf('.') > -1)
            return (int) Math.round(stringToDouble(string, defaultValue));

        try {
            return validator.isMatchRegex(integerPattern, string) ? Integer.parseInt(string) : defaultValue;
        } catch (Exception e) {
            logger.warn(e, "将对象[{}]转化为int数值时发生异常！", object);

            return defaultValue;
        }
    }

    @Override
    public int hexToInt(String hex) {
        return hexToInt(hex, 0);
    }

    @Override
    public int hexToInt(String hex, int defaultValue) {
        if (validator.isEmpty(hex))
            return defaultValue;

        try {
            return Integer.parseInt(hex, 16);
        } catch (Throwable throwable) {
            logger.warn(throwable, "转化十六进制数据[{}]为整数时发生异常！", hex);

            return defaultValue;
        }
    }

    @Override
    public long toLong(Object object) {
        return toLong(object, 0L);
    }

    @Override
    public long toLong(Object object, long defaultValue) {
        if (validator.isEmpty(object))
            return defaultValue;

        if (object instanceof Long l)
            return l;

        if (object instanceof Integer n)
            return n;

        if (object instanceof Float f)
            return Math.round(f);

        if (object instanceof Double d)
            return Math.round(d);

        String string = toString(object);
        if (string.indexOf('.') > -1)
            return Math.round(stringToDouble(string, defaultValue));

        try {
            return validator.isMatchRegex(integerPattern, string) ? Long.parseLong(string) : defaultValue;
        } catch (Exception e) {
            logger.warn(e, "将对象[{}]转化为long数值时发生异常！", object);

            return defaultValue;
        }
    }

    @Override
    public float toFloat(Object object) {
        return toFloat(object, 0.0F);
    }

    @Override
    public float toFloat(Object object, float defaultValue) {
        if (validator.isEmpty(object))
            return defaultValue;

        if (object instanceof Float f)
            return f;

        if (object instanceof Double d)
            return (float) (double) d;

        if (object instanceof Integer n)
            return n;

        if (object instanceof Long l)
            return (float) (long) l;

        String string = toString(object);
        try {
            return validator.isMatchRegex(floatPattern, string) ? Float.parseFloat(string) : defaultValue;
        } catch (Exception e) {
            logger.warn(e, "将对象[{}]转化为float数值时发生异常！", object);

            return defaultValue;
        }
    }

    @Override
    public double toDouble(Object object) {
        return toDouble(object, 0.0D);
    }

    @Override
    public double toDouble(Object object, double defaultValue) {
        if (validator.isEmpty(object))
            return defaultValue;

        if (object instanceof Double d)
            return d;

        if (object instanceof Float f)
            return f;

        if (object instanceof Integer n)
            return n;

        if (object instanceof Long l)
            return l;

        return stringToDouble(toString(object), defaultValue);
    }

    private double stringToDouble(String string, double defaultValue) {
        try {
            return validator.isMatchRegex(floatPattern, string) ? Double.parseDouble(string) : defaultValue;
        } catch (Exception e) {
            logger.warn(e, "将对象[{}]转化为double数值时发生异常！", string);

            return defaultValue;
        }
    }

    private String toString(Object object) {
        return (object instanceof String string ? string : object.toString()).replaceAll(",", "");
    }

    @Override
    public int[] toInts(String string) {
        return toInts(string, 0);
    }

    @Override
    public int[] toInts(String string, int defaultValue) {
        if (validator.isEmpty(string))
            return new int[0];

        return toInts(string.split(","), defaultValue);
    }

    @Override
    public int[] toInts(String[] array) {
        return toInts(array, 0);
    }

    @Override
    public int[] toInts(String[] array, int defaultValue) {
        if (validator.isEmpty(array))
            return new int[0];

        int[] ns = new int[array.length];
        for (int i = 0; i < ns.length; i++)
            ns[i] = toInt(array[i], defaultValue);

        return ns;
    }

    @Override
    public double[] toDoubles(String string) {
        return toDoubles(string, 0.0D);
    }

    @Override
    public double[] toDoubles(String string, double defaultValue) {
        if (validator.isEmpty(string))
            return new double[0];

        return toDoubles(string.split(","), defaultValue);
    }

    @Override
    public double[] toDoubles(String[] array) {
        return toDoubles(array, 0.0D);
    }

    @Override
    public double[] toDoubles(String[] array, double defaultValue) {
        if (validator.isEmpty(array))
            return new double[0];

        double[] ns = new double[array.length];
        for (int i = 0; i < ns.length; i++)
            ns[i] = toDouble(array[i], defaultValue);

        return ns;
    }

    @Override
    public String toString(Number number) {
        return toString(number, "0");
    }

    @Override
    public String toString(Number number, String format) {
        return formats.computeIfAbsent(format, DecimalFormat::new).format(number);
    }

    @Override
    public String toChineseString(String number, boolean big) {
        char[] chars = big ? bigChineses : chineses;
        String string = toChineseString(chars, number);
        if (number.length() < 9)
            return string;

        return string.replaceAll("" + chars[14] + chars[13], "" + chars[14]);
    }

    private String toChineseString(char[] chars, String number) {
        StringBuilder sb = new StringBuilder();
        for (int i = 1, length = number.length(); i <= length; i++) {
            int n = number.charAt(length - i) - '0';
            int unit = (i - 1) % 4;
            if (unit == 0 && i > 1)
                sb.insert(0, chars[13 + ((i - 1) / 4 - 1) % 2]);
            if (n == 0) {
                if (unit == 0 || sb.length() == 0)
                    continue;

                char ch = sb.charAt(0);
                if (ch == chars[13] || ch == chars[14])
                    continue;

                sb.insert(0, chars[0]);

                continue;
            }

            if (unit > 0)
                sb.insert(0, chars[9 + unit]);
            sb.insert(0, chars[n]);
        }
        if (sb.length() > 0 && sb.charAt(0) == chars[1] && sb.charAt(1) == chars[10])
            return sb.substring(1);

        return sb.toString();
    }

    @Override
    public int compareBigDecimal(String bd1, String bd2) {
        return new BigDecimal(bd1).compareTo(new BigDecimal(bd2));
    }

    @Override
    public String addBigDecimal(String bd1, String bd2) {
        try {
            return new BigDecimal(bd1).add(new BigDecimal(bd2)).toString();
        } catch (Throwable throwable) {
            logger.warn(throwable, "两个BigDecimal[{}:{}]相加异常！", bd1, bd2);

            return null;
        }
    }

    @Override
    public String subtractBigDecimal(String bd1, String bd2) {
        try {
            return new BigDecimal(bd1).subtract(new BigDecimal(bd2)).toString();
        } catch (Throwable throwable) {
            logger.warn(throwable, "两个BigDecimal[{}:{}]相减异常！", bd1, bd2);

            return null;
        }
    }

    @Override
    public String multiplyBigDecimal(String bd1, String bd2) {
        try {
            return new BigDecimal(bd1).multiply(new BigDecimal(bd2)).toString();
        } catch (Throwable throwable) {
            logger.warn(throwable, "两个BigDecimal[{}:{}]相乘异常！", bd1, bd2);

            return null;
        }
    }

    @Override
    public String divideBigDecimal(String bd1, String bd2, int scale) {
        try {
            return new BigDecimal(bd1).divide(new BigDecimal(bd2), scale, RoundingMode.CEILING).toString();
        } catch (Throwable throwable) {
            logger.warn(throwable, "两个BigDecimal[{}:{}:{}]相乘异常！", bd1, bd2, scale);

            return null;
        }
    }
}
