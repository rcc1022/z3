package org.lpw.photon.util;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component("photon.util.converter")
public class ConverterImpl implements Converter {
    private static final String[] BIT_SIZE_FORMAT = {"0 B", "0.00 K", "0.00 M", "0.00 G", "0.00 T"};

    @Inject
    private Context context;
    @Inject
    private Validator validator;
    @Inject
    private Numeric numeric;
    @Inject
    private DateTime dateTime;
    @Inject
    private Codec codec;
    @Inject
    private Logger logger;

    @Override
    public String toString(Object object) {
        if (validator.isEmpty(object))
            return "";

        if (object instanceof Object[] objects)
            return arrayString(objects, ",");


        if (object.getClass().isArray()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0, length = Array.getLength(object); i < length; i++)
                sb.append(',').append(Array.get(object, i));

            return sb.length() == 0 ? "" : sb.substring(1);
        }

        if (object instanceof Iterable iterable)
            return iterableString(iterable, ",");

        if (object instanceof Map map)
            return mapString(map, ",");

        if (object instanceof Date date)
            return dateTime.toString(date);

        return object.toString();
    }

    @Override
    public String toString(Object number, int decimal, int point) {
        return numeric.toString(numeric.toLong(number) * Math.pow(0.1D, decimal), "0." + "0".repeat(Math.max(0, point)));
    }

    @Override
    public String toString(Object[] array, String separator) {
        return validator.isEmpty(array) ? "" : arrayString(array, separator);
    }

    private String arrayString(Object[] array, String separator) {
        StringBuilder sb = new StringBuilder();
        for (Object object : array)
            sb.append(separator).append(toString(object));

        return sb.length() == 0 ? "" : sb.substring(separator.length());
    }

    @Override
    public String toString(Iterable<?> iterable, String separator) {
        return validator.isEmpty(iterable) ? "" : iterableString(iterable, separator);
    }

    private String iterableString(Iterable<?> iterable, String separator) {
        StringBuilder sb = new StringBuilder();
        iterable.forEach(obj -> sb.append(separator).append(toString(obj)));

        return sb.length() == 0 ? "" : sb.substring(separator.length());
    }

    @Override
    public String toString(Map<?, ?> map, String separator) {
        return validator.isEmpty(map) ? "" : mapString(map, separator);
    }

    private String mapString(Map<?, ?> map, String separator) {
        StringBuilder sb = new StringBuilder();
        map.forEach((key, value) -> sb.append(separator).append(toString(key)).append('=').append(toString(value)));

        return sb.length() == 0 ? "" : sb.substring(separator.length());
    }

    @Override
    public String toString(byte[] bytes, String charset) {
        try {
            return new String(bytes, context.getCharset(charset));
        } catch (UnsupportedEncodingException e) {
            logger.warn(e, "将byte数组转化为字符串[{}]时发生异常！", charset);

            return null;
        }
    }

    @Override
    public String[] toArray(String string, String separator) {
        if (string == null)
            return new String[0];

        if (separator == null || !string.contains(separator))
            return new String[]{string};

        List<String> list = new ArrayList<>();
        int length = separator.length();
        int index = 0;
        for (int indexOf; index <= (indexOf = string.indexOf(separator, index)); index = indexOf + length) {
            list.add(string.substring(index, indexOf));
        }
        if (index <= string.length())
            list.add(string.substring(index));

        return list.toArray(new String[0]);
    }

    @Override
    public String[][] toArray(String string, String[] separator) {
        if (validator.isEmpty(string) || validator.isEmpty(separator) || separator.length < 2 || separator[0] == null || separator[0].length() == 0
                || separator[1] == null || separator[1].length() == 0)
            return new String[0][0];

        List<String> list = new ArrayList<>();
        for (String str : toArray(string, separator[0]))
            if (string.contains(separator[1]))
                list.add(str);

        if (list.isEmpty())
            return new String[0][0];

        String[][] array = new String[list.size()][];
        for (int i = 0; i < array.length; i++)
            array[i] = toArray(list.get(i), separator[1]);

        return array;
    }

    @Override
    public <T> Set<T> toSet(T[] array) {
        Set<T> set = new HashSet<>();
        if (array != null)
            Collections.addAll(set, array);

        return set;
    }

    @Override
    public String toBitSize(long size) {
        return toBitSize(size < 0 ? 0 : size, 0);
    }

    private String toBitSize(double size, int pattern) {
        if (size >= 1024 && pattern < BIT_SIZE_FORMAT.length - 1)
            return toBitSize(size / 1024, pattern + 1);

        return numeric.toString(size, BIT_SIZE_FORMAT[pattern]);
    }

    @Override
    public long toBitSize(String size) {
        if (validator.isEmpty(size))
            return -1L;

        double value = numeric.toDouble(size.substring(0, size.length() - 1).trim(), -1.0D);
        char unit = size.toLowerCase().charAt(size.length() - 1);
        if (unit == 't')
            return Math.round(value * 1024 * 1024 * 1024 * 1024);

        if (unit == 'g')
            return Math.round(value * 1024 * 1024 * 1024);

        if (unit == 'm')
            return Math.round(value * 1024 * 1024);

        if (unit == 'k')
            return Math.round(value * 1024);

        return Math.round(numeric.toDouble(size.trim(), -1.0D));
    }

    @Override
    public boolean toBoolean(Object object) {
        if (validator.isEmpty(object))
            return false;

        try {
            return Boolean.parseBoolean(object.toString());
        } catch (Exception e) {
            logger.warn(e, "将对象[{}]转化为布尔值时发生异常！", object);

            return false;
        }
    }

    @Override
    public String toFirstLowerCase(String string) {
        return toFirstCase(string, 'A', 'Z', 'a' - 'A');
    }

    @Override
    public String toFirstUpperCase(String string) {
        return toFirstCase(string, 'a', 'z', 'A' - 'a');
    }

    private String toFirstCase(String string, char start, char end, int shift) {
        if (validator.isEmpty(string))
            return string;

        char ch = string.charAt(0);
        if (ch < start || ch > end)
            return string;

        char[] chars = string.toCharArray();
        chars[0] += shift;

        return new String(chars);
    }

    @Override
    public Map<String, String> toParameterMap(String parameters) {
        Map<String, String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder("&").append(parameters);
        String value = "";
        for (int indexOf; (indexOf = sb.lastIndexOf("&")) > -1; ) {
            String string = sb.substring(indexOf + 1);
            sb.delete(indexOf, sb.length());
            if ((indexOf = string.indexOf('=')) == -1) {
                value = "&" + string + value;

                continue;
            }
            map.put(string.substring(0, indexOf), codec.decodeUrl(string.substring(indexOf + 1) + value, null));
            value = "";
        }

        return map;
    }

    @Override
    public String md5ToId(String md5) {
        StringBuilder sb = new StringBuilder(md5);
        sb.insert(20, '-');
        sb.insert(16, '-');
        sb.insert(12, '-');
        sb.insert(8, '-');

        return sb.toString();
    }
}
