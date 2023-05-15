package org.lpw.photon.util;

/**
 * 时间单位。
 */
public enum TimeUnit {
    /**
     * 毫秒。
     */
    MilliSecond(1L),
    /**
     * 秒。
     */
    Second(1000L),
    /**
     * 分钟。
     */
    Minute(60L * 1000),
    /**
     * 小时。
     */
    Hour(60L * 60 * 1000),
    /**
     * 天。
     */
    Day(24L * 60 * 60 * 1000);

    long time;

    TimeUnit(long time) {
        this.time = time;
    }

    /**
     * 获取时间值。
     *
     * @return 时间值。
     */
    public long getTime() {
        return time;
    }

    /**
     * 获取时间值。
     *
     * @param n 时间。
     * @return 时间值。
     */
    public long getTime(int n) {
        return n * time;
    }

    /**
     * 获取当前时间数。
     *
     * @return 当前时间数。
     */
    public long now() {
        return System.currentTimeMillis() / time;
    }

    /**
     * 获取当前时间数。
     *
     * @param n 时间。
     * @return 当前时间数。
     */
    public long now(int n) {
        return System.currentTimeMillis() / n / time;
    }
}
