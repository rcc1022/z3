package org.lpw.photon.util;

/**
 * 数值转换器。
 */
public interface Numeric {
    /**
     * 将对象转化为int数值。
     *
     * @param object 要转化的对象。
     * @return int数值；如果转化失败则返回0。
     */
    int toInt(Object object);

    /**
     * 将对象转化为int数值。
     *
     * @param object       要转化的对象。
     * @param defaultValue 默认值。
     * @return int数值；如果转化失败则返回默认值。
     */
    int toInt(Object object, int defaultValue);

    /**
     * 将十六进制数据转化为整数。
     *
     * @param hex 十六进制数据。
     * @return 整数；如果转化失败则返回0。
     */
    int hexToInt(String hex);

    /**
     * 将十六进制数据转化为整数。
     *
     * @param hex          十六进制数据。
     * @param defaultValue 默认值。
     * @return 整数；如果转化失败则返回默认值。
     */
    int hexToInt(String hex, int defaultValue);

    /**
     * 将对象转化为long数值。
     *
     * @param object 要转化的对象。
     * @return long数值；如果转化失败则返回0。
     */
    long toLong(Object object);

    /**
     * 将对象转化为long数值。
     *
     * @param object       要转化的对象。
     * @param defaultValue 默认值。
     * @return long数值；如果转化失败则返回默认值。
     */
    long toLong(Object object, long defaultValue);

    /**
     * 将对象转化为float数值。
     *
     * @param object 要转化的对象。
     * @return float数值；如果转化失败则返回0.0。
     */
    float toFloat(Object object);

    /**
     * 将对象转化为float数值。
     *
     * @param object       要转化的对象。
     * @param defaultValue 默认值。
     * @return float数值；如果转化失败则返回默认值。
     */
    float toFloat(Object object, float defaultValue);

    /**
     * 将对象转化为double数值。
     *
     * @param object 要转化的对象。
     * @return double数值；如果转化失败则返回0.0。
     */
    double toDouble(Object object);

    /**
     * 将对象转化为double数值。
     *
     * @param object       要转化的对象。
     * @param defaultValue 默认值。
     * @return double数值；如果转化失败则返回默认值。
     */
    double toDouble(Object object, double defaultValue);

    /**
     * 将字符串转化为int数组，字符串中数值以逗号分割。
     *
     * @param string 数值组字符串。
     * @return int数组；如果转化失败则返回空数组。
     */
    int[] toInts(String string);

    /**
     * 将字符串转化为int数组，字符串中数值以逗号分割。
     *
     * @param string       数值组字符串。
     * @param defaultValue 默认值。
     * @return int数组；如果转化失败则返回空数组。
     */
    int[] toInts(String string, int defaultValue);

    /**
     * 将数值字符串数组转化为数值数组。
     *
     * @param array 数值字符串数组。
     * @return int数组；如果元素转化失败则默认为0。
     */
    int[] toInts(String[] array);

    /**
     * 将数值字符串数组转化为数值数组。
     *
     * @param array        数值字符串数组。
     * @param defaultValue 默认值。
     * @return int数组；如果元素转化失败则默认为0。
     */
    int[] toInts(String[] array, int defaultValue);

    /**
     * 将字符串转化为double数组，字符串中数值以逗号分割。
     *
     * @param string 数值组字符串。
     * @return double数组；如果转化失败则返回空数组。
     */
    double[] toDoubles(String string);

    /**
     * 将字符串转化为double数组，字符串中数值以逗号分割。
     *
     * @param string       数值组字符串。
     * @param defaultValue 默认值。
     * @return double数组；如果转化失败则返回空数组。
     */
    double[] toDoubles(String string, double defaultValue);

    /**
     * 将数值字符串数组转化为数值数组。
     *
     * @param array 数值字符串数组。
     * @return double数组；如果元素转化失败则默认为0。
     */
    double[] toDoubles(String[] array);

    /**
     * 将数值字符串数组转化为数值数组。
     *
     * @param array        数值字符串数组。
     * @param defaultValue 默认值。
     * @return double数组；如果元素转化失败则默认为0。
     */
    double[] toDoubles(String[] array, double defaultValue);

    /**
     * 按指定格式将数值格式化为字符串。
     *
     * @param number 要进行格式化的数值。
     * @return 格式化后的数值字符串。
     */
    String toString(Number number);

    /**
     * 按指定格式将数值格式化为字符串。
     *
     * @param number 要进行格式化的数值。
     * @param format 目标格式。
     * @return 格式化后的数值字符串。
     */
    String toString(Number number, String format);

    /**
     * 转化为中文数值字符串。如：一万二千三百四十五/壹万贰仟叁佰肆拾伍。
     *
     * @param number 数值字符串。
     * @return 中文数值字符串。
     */
    String toChineseString(String number, boolean big);

    /**
     * 比较两个BigDecimal字符串大小。
     *
     * @param bd1 BigDecimal字符串。
     * @param bd2 BigDecimal字符串。
     * @return 如果相等则返回0, 如果前者大于后者则返回1；如果前者小于后者，则返回-1；
     */
    int compareBigDecimal(String bd1, String bd2);

    /**
     * 两个BigDecimal字符串相加。
     *
     * @param bd1 BigDecimal字符串。
     * @param bd2 BigDecimal字符串。
     * @return 相加结果；失败则返回null。
     */
    String addBigDecimal(String bd1, String bd2);

    /**
     * 两个BigDecimal字符串相减。
     *
     * @param bd1 BigDecimal字符串。
     * @param bd2 BigDecimal字符串。
     * @return 相减结果；失败则返回null。
     */
    String subtractBigDecimal(String bd1, String bd2);

    /**
     * 两个BigDecimal字符串相乘。
     *
     * @param bd1 BigDecimal字符串。
     * @param bd2 BigDecimal字符串。
     * @return 相乘结果；失败则返回null。
     */
    String multiplyBigDecimal(String bd1, String bd2);

    /**
     * 两个BigDecimal字符串相除。
     *
     * @param bd1   BigDecimal字符串。
     * @param bd2   BigDecimal字符串。
     * @param scale 结果保留小数点位数。
     * @return 相除结果；失败则返回null。
     */
    String divideBigDecimal(String bd1, String bd2, int scale);
}
