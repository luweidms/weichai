package com.youming.youche.finance.commons.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

import static com.youming.youche.commons.util.DateUtil.getMillis;

/**
 * @author hzx
 * @date 2022/2/8 11:06
 */
public class DateUtil {

    public static String DATETIME_FORMAT1 = "yyyy-MM-dd HH:mm";
    public static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_ZEROTIME_FORMAT = "yyyy-MM-dd 00:00:00";
    public static String DATE_ZEROTIME_FORMAT2 = "yyyyMMdd000000";
    public static String DATE_FULLTIME_FORMAT = "yyyy-MM-dd 23:59:59";
    public static String DATE_FULLTIME_FORMAT2 = "yyyyMMdd235959";
    public static String DATETIME12_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DATETIME12_FORMAT2 = "yyyyMMddhhmmss";
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static String DATE_FORMAT2 = "yyyyMMdd";
    public static String YEAR_MONTH_FORMAT = "yyyy-MM";
    public static String YEAR_MONTH_FORMAT2 = "yyyyMM";
    public static String YEAR_MONTH_FIRSTDAY = "yyyy-MM-01";
    public static String YEAR_FORMAT = "yyyy";
    public static String MONTH_FORMAT = "MM";
    public static String DAY_FORMAT = "dd";
    public static String TIME_FORMAT = "HH:mm:ss";
    public static String TIME_FORMAT2 = "HHmmss";
    public static String TIME12_FORMAT = "HH:mm:ss";
    public static String TIME12_FORMAT2 = "HHmmss";
    public static String DATETIME_SLASH_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public static String DATE_SLASH_FORMAT = "yyyy/MM/dd";

    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        } else {
            if (format.indexOf("h") > 0) {
                format = format.replace('h', 'H');
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            return simpleDateFormat.format(date);
        }
    }

    public static String formatDateByFormat(Date date, String format) {
        String result = "";
        if (date != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                result = sdf.format(date);
            } catch (Exception var4) {
                var4.printStackTrace();
            }
        }

        return result;
    }

    public static LocalDateTime localDateTime (Date date){
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        return localDateTime;
    }
    /**
     * LocalDateTime转换为Date
     * @param localDateTime
     * @return
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(1);
    }
    public static Date formatStringToDate(String str) throws ParseException {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        date = sdf.parse(str);
        return date;
    }

    public static Date addMinis(Date date, int minis) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(getMillis(date) + (long)minis * 60L * 1000L);
            return c.getTime();
        }
    }
    public static Date addDate(Date date, int day) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(getMillis(date) + (long)day * 24L * 3600L * 1000L);
            return c.getTime();
        }
    }

}
