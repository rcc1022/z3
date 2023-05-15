package org.lpw.photon.util;

import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.sql.Timestamp;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component("photon.util.date-time")
public class DateTimeImpl implements DateTime {
    private final String DATE_FORMAT = "yyyy-MM-dd";
    private final String DATE_TIME_FORMAT = DATE_FORMAT + " HH:mm:ss";
    private final String DATE_T_TIME_FORMAT = DATE_FORMAT + "THH:mm:ss";

    @Inject
    private Validator validator;
    @Inject
    private Logger logger;
    private final Map<String, FastDateFormat> dateFormatMap = new ConcurrentHashMap<>();

    @Override
    public java.sql.Date today() {
        return new java.sql.Date(System.currentTimeMillis());
    }

    @Override
    public Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }

    @Override
    public Timestamp getStart(Date date) {
        return getStart(date, false);
    }

    @Override
    public Timestamp getStart(Date date, boolean month) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        if (month)
            calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return new Timestamp(calendar.getTime().getTime());
    }

    @Override
    public Timestamp getEnd(Date date) {
        return getEnd(date, false);
    }

    @Override
    public Timestamp getEnd(Date date, boolean month) {
        if (date == null)
            return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        if (month) {
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            calendar.set(Calendar.DAY_OF_MONTH, 0);
        }
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return new Timestamp(calendar.getTime().getTime());
    }

    @Override
    public Timestamp getStart(String string) {
        return getStart(string, false);
    }

    @Override
    public Timestamp getStart(String string, boolean month) {
        return getStart(toDate(string), month);
    }

    @Override
    public Timestamp getEnd(String string) {
        return getEnd(string, false);
    }

    @Override
    public Timestamp getEnd(String string, boolean month) {
        return getEnd(toDate(string), month);
    }

    @Override
    public Timestamp toTime(String string) {
        return toTimestamp(toDate(string));
    }

    @Override
    public Timestamp toTime(String string, String pattern) {
        return toTimestamp(toDate(string, pattern));
    }

    private Timestamp toTimestamp(Date date) {
        return date == null ? null : new Timestamp(date.getTime());
    }

    @Override
    public String toString(Date date, String format) {
        return date == null ? "" : getDateFormat(format).format(date);
    }

    @Override
    public String toString(Date date) {
        return toString(date, date instanceof Timestamp ? DATE_TIME_FORMAT : DATE_FORMAT);
    }

    @Override
    public Date toDate(Object date) {
        if (validator.isEmpty(date))
            return null;

        if (date instanceof Date d)
            return d;

        if (date instanceof LocalDateTime ldt)
            return new Date(ldt.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());

        if (date instanceof String string) {
            switch (string.length()) {
                case 10 -> {
                    return toDate(string, DATE_FORMAT);
                }
                case 19 -> {
                    if (string.indexOf(' ') > -1)
                        return toDate(string, DATE_TIME_FORMAT);

                    if (string.indexOf('T') > -1)
                        return toDate(string, DATE_T_TIME_FORMAT);
                }
            }
        }

        return null;
    }

    @Override
    public Date toDate(String date, String format) {
        if (validator.isEmpty(date) || validator.isEmpty(format))
            return null;

        try {
            return getDateFormat(format).parse(date);
        } catch (ParseException e) {
            logger.warn(e, "使用格式[{}]将字符串[{}]转化为日期值时发生异常！", format, date);

            return null;
        }
    }

    private FastDateFormat getDateFormat(String format) {
        return dateFormatMap.computeIfAbsent(format, FastDateFormat::getInstance);
    }

    @Override
    public java.sql.Date toSqlDate(String date) {
        return toSqlDate(date, DATE_FORMAT);
    }

    @Override
    public java.sql.Date toSqlDate(String date, String format) {
        Date d = toDate(date, format);

        return d == null ? null : new java.sql.Date(d.getTime());
    }

    @Override
    public java.sql.Date[] toDateRange(String[] dates) {
        java.sql.Date[] ds = new java.sql.Date[2];
        if (dates != null)
            for (int i = 0; i < ds.length; i++)
                if (dates.length > i)
                    ds[i] = toSqlDate(dates[i]);

        return ds;
    }

    @Override
    public Timestamp[] toTimeRange(String[] dates) {
        Timestamp[] times = new Timestamp[2];
        if (dates != null)
            for (int i = 0; i < times.length; i++)
                if (dates.length > i)
                    times[i] = dates[i].length() == 19 ? toTime(dates[i]) : (i == 0 ? getStart(dates[i]) : getEnd(dates[i]));

        return times;
    }

    @Override
    public Timestamp[] toTimeRange(Date date) {
        return new Timestamp[]{getStart(date), getEnd(date)};
    }

    @Override
    public long toLong(Date date) {
        return date == null ? 0L : date.getTime();
    }
}
