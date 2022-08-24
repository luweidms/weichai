//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.youming.youche.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public static String YEAR_MONTH_FORMAT3 = "yyyy年MM月";
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
    public static String DTAE_DAY1="HH:mm";
    public DateUtil() {
    }

    public static Timestamp convertStrToTimestamp(String timeStampStr) {
        Timestamp returnT = null;
        if (timeStampStr != null && !timeStampStr.trim().equals("")) {
            returnT = new Timestamp(parseDate(timeStampStr).getTime());
        }

        return returnT;
    }

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

    public static Date parseDate(String str) {
        if (str != null && str.trim().length() != 0) {
            if (str.length() == 10) {
                return parseDate(str, "yyyy-MM-dd");
            } else if (str.length() == 13) {
                return parseDate(str, "yyyy-MM-dd HH");
            } else if (str.length() == 16) {
                return parseDate(str, "yyyy-MM-dd HH:mm");
            } else if (str.length() == 19) {
                return parseDate(str, "yyyy-MM-dd HH:mm:ss");
            } else {
                return str.length() >= 21 ? parseDate(str, "yyyy-MM-dd HH:mm:ss.S") : null;
            }
        } else {
            return null;
        }
    }

    public static Date parseDate(String str, String format) {
        try {
            if (str != null && !str.equals("")) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
                return simpleDateFormat.parse(str);
            } else {
                return null;
            }
        } catch (Exception var3) {
            var3.printStackTrace();
            return new Date();
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

    public static String formatDate(Date date) {
        return formatDateByFormat(date, DATE_FORMAT);
    }

    public static Date formatStringToDate(String str) throws ParseException {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        date = sdf.parse(str);
        return date;
    }

    public static Date formatStringToDate(String str, String format) throws ParseException {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        date = sdf.parse(str);
        return date;
    }

    public static String getCurrDate() {
        Calendar now = Calendar.getInstance();
        return getDateStr(now);
    }

    public static String getCurrDateTime() {
        Calendar now = Calendar.getInstance();
        return getDateStr(now) + " " + getHour(now) + ":" + getMinute(now) + ":" + getSecond(now);
    }

    public static String getCurrDateTimeBegin() {
        Calendar now = Calendar.getInstance();
        return getDateStr(now) + " 00:00:00";
    }

    public static String getCurrDateTimeEnd() {
        Calendar now = Calendar.getInstance();
        return getDateStr(now) + " 23:59:59";
    }

    public static String getFirstDayDate() {
        Calendar now = Calendar.getInstance();
        return getYear(now) + "-" + getMonth(now) + "-01";
    }

    public static String getFirstDayDateTime() {
        return getFirstDayDate() + " 00:00:00";
    }

    public static String getDateStr(Calendar cal) {
        return getYear(cal) + "-" + getMonth(cal) + "-" + getDay(cal);
    }

    public static String getYear(Calendar cal) {
        return String.valueOf(cal.get(1));
    }

    public static String getMonth(Calendar cal) {
        return strLen(String.valueOf(cal.get(2) + 1), 2);
    }

    public static String getDay(Calendar cal) {
        return strLen(String.valueOf(cal.get(5)), 2);
    }

    public static String getHour(Calendar cal) {
        return strLen(String.valueOf(cal.get(11)), 2);
    }

    public static String getMinute(Calendar cal) {
        return strLen(String.valueOf(cal.get(12)), 2);
    }

    public static String getSecond(Calendar cal) {
        return strLen(String.valueOf(cal.get(13)), 2);
    }

    public static int getYear(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(1);
    }

    public static int getMonth(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(2) + 1;
    }

    public static int getDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(5);
    }

    public static int getHour(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(11);
    }

    public static int getMinute(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(12);
    }

    public static int getSecond(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(13);
    }

    public static long getMillis(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getTimeInMillis();
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

    public static Date addHour(Date date, int hour) {
        if (date == null) {
            return null;
        } else {
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(getMillis(date) + (long)hour * 3600L * 1000L);
            return c.getTime();
        }
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

    public static Date diffDate(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(getMillis(date) - (long)day * 24L * 3600L * 1000L);
        return c.getTime();
    }

    public static Date diffMonth(Date date, int month) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -month);
        return c.getTime();
    }

    public static Date addMonth(Date date, int month) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(date);
        rightNow.add(2, month);
        return rightNow.getTime();
    }

    public static int diffDate(Date date, Date date1) {
        return (int)((getMillis(date) - getMillis(date1)) / 86400000L);
    }



    public static int getWeekFirstDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return 1;
    }

    public static Date getMaxDate(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        } else if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        } else {
            return date1.before(date2) ? date2 : date1;
        }
    }

    public static Date getMinDate(Date date1, Date date2) {
        if (date1 == null && date2 == null) {
            return null;
        } else if (date1 == null) {
            return date2;
        } else if (date2 == null) {
            return date1;
        } else {
            return date1.before(date2) ? date1 : date2;
        }
    }

    public static String cvtToTimeStr(Timestamp dtDate) {
        return cvtFormattedDate(dtDate, TIME_FORMAT);
    }

    public static String cvtFormattedDate(Timestamp dtDate, String strFormatTo, String defaultValue) {
        String dateString = cvtFormattedDate(dtDate, strFormatTo);
        return dateString != null && !"".equals(dateString) ? dateString : defaultValue;
    }

    public static String cvtFormattedDate(Timestamp dtDate, String strFormatTo) {
        if (dtDate == null) {
            return "";
        } else {
            strFormatTo = strFormatTo.replace('/', '-');

            try {
                if (dtDate.getTime() != getZeroDate().getTime() && dtDate.getTime() != 0L && dtDate.getTime() != -62170185600000L) {
                    SimpleDateFormat formatter = new SimpleDateFormat(strFormatTo);
                    return formatter.format(dtDate);
                } else {
                    String ret = "";
                    if (strFormatTo.equals("yyyy-MM-dd")) {
                        ret = "0000-00-00";
                    } else if (strFormatTo.equals("yyyy-MM")) {
                        ret = "0000-00";
                    } else if (strFormatTo.equals("yyyyMM")) {
                        ret = "000000";
                    } else if (strFormatTo.equals("HH:mm:ss")) {
                        ret = "00:00:00";
                    } else {
                        ret = "0000-00-00 00:00:00";
                    }

                    return ret;
                }
            } catch (Exception var3) {
                System.out.println("转换日期字符串格式时出错;" + var3.getMessage());
                return "";
            }
        }
    }

    public static java.sql.Date parseDate2SqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    public static Timestamp getZeroDate() {
        return getTimeStamp("2000-01-01 00:00:00");
    }

    public static Timestamp getTimeStamp(String dateDesc) {
        Date date = parseDate(dateDesc);
        Timestamp ts = new Timestamp(date.getTime());
        return ts;
    }

    public static String strLen(String s, int len) {
        if (s == null) {
            s = "";
        } else {
            s = s.trim();
        }

        int strLen = s.length();

        for(int i = 0; i < len - strLen; ++i) {
            s = "0" + s;
        }

        return s;
    }

    public static Date asDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }


    public static LocalDate asLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1,Date date2){
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000*3600*24));
        return days;
    }

    /**
     * 获取指定日期的上个月第一天和上个月最后一天
     *
     * @throws ParseException
     */
    public static Map<String, String> getLastOneMonthDay(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        Map<String, String> map = new HashMap<>();
        // 上月起始
        Calendar lastMonthFirstDateCal = Calendar.getInstance();
        if(date != null){
            lastMonthFirstDateCal.setTime(date);
        }
        lastMonthFirstDateCal.add(Calendar.MONTH, -1);
        lastMonthFirstDateCal.set(Calendar.DAY_OF_MONTH, 1);
        String lastMonthFirstTime = format.format(lastMonthFirstDateCal.getTime());
        System.out.println("上月起始:" + lastMonthFirstTime);
        // 上月末尾
        Calendar lastMonthEndDateCal = Calendar.getInstance();
        lastMonthEndDateCal.add(Calendar.MONTH, -1);
        lastMonthEndDateCal.set(
                Calendar.DAY_OF_MONTH, lastMonthEndDateCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastMonthEndTime = format.format(lastMonthEndDateCal.getTime());
        System.out.println("上月末尾:" + lastMonthEndTime);
        map.put("beginDate", lastMonthFirstTime);
        map.put("endDate", lastMonthEndTime);
        return map;
    }
    /**
     * 获取某段时间内的所有日期
     * @param startDate yyyy-MM-dd
     * @param endDate yyyy-MM-dd
     * @return yyyy-MM-dd
     */
    public static List<Date> findDates(Date startDate, Date endDate) {
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(startDate);
        List dateList = new ArrayList();
        //别忘了，把起始日期加上
        dateList.add(startDate);
        // 此日期是否在指定日期之后
        while (endDate.after(cStart.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cStart.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(cStart.getTime());
        }
        return dateList;
    }

    public static LocalDateTime parseLocalDateTime(String time, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, dateTimeFormatter);
    }

    public static String formatLocalDateTime(LocalDateTime time, String format) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        return time.format(dateTimeFormatter);
    }

    /**
     * 获取指定日期上个月第一天
     */
    public static String getLastMonthFirstDay(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, -1);
        ca.set(Calendar.DAY_OF_MONTH,1);
        return dateFormat.format(ca.getTime());
    }

    /**
     * 获取指定日期上个月最后一天
     */
    public static String getLastMonthLastDay(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_MONTH,0);
        return dateFormat.format(ca.getTime());
    }
}
