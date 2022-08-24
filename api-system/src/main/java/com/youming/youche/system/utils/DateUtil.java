package com.youming.youche.system.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 温泉
 * @ClassName: DateUtil
 * @Description: 时间工具类-原数字化车队
 * @date 2012-2-22
 */

public class DateUtil {

    /**
     * 缺省日期格式
     */
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATE_FORMAT2 = "yyyyMMdd";

    /**
     * 缺省时间格式
     */
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";

    /**
     * 缺省月格式
     */
    public static final String DEFAULT_MONTH = "MONTH";

    /**
     * 缺省年格式
     */
    public static final String DEFAULT_YEAR = "YEAR";

    /**
     * 缺省日格式
     */
    public static final String DEFAULT_DATE = "DAY";

    /**
     * 缺省小时格式
     */
    public static final String DEFAULT_HOUR = "HOUR";

    /**
     * 缺省分钟格式
     */
    public static final String DEFAULT_MINUTE = "MINUTE";

    /**
     * 缺省秒格式
     */
    public static final String DEFAULT_SECOND = "SECOND";

    /**
     * 缺省长日期格式
     */
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH-mm";

    public static final String MONTH_DATE = "MM-dd HH:mm:ss";

    /**
     * 缺省长日期格式
     */
    public static final String DEFAULT_DATETIME_FORMAT_ONE = "yyyy-MM-dd HH:mm";

    /**
     * 缺省长日期格式,精确到秒
     */
    public static final String DEFAULT_DATETIME_FORMAT_SEC = "yyyy-MM-dd HH:mm:ss";

    /**
     * 缺省长日期格式,精确到毫秒
     */
    public static final String DEFAULT_DATETIME_FORMAT_MILL = "yyyy-MM-dd HH:mm:ss.S";

    /**
     * 星期数组
     */
    public static final String[] WEEKS = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * @Fields DAY_TO_MILLIS : 一天对应的毫秒数
     */
    public static final long DAY_TO_MILLIS = 24 * 60 * 60 * 1000;

    /**
     * @param time    时间
     * @param format  时间格式
     * @param format1 需要格式成什么格式
     * @param @throws ParseException    设定文件
     * @return String    返回类型
     * @throws
     * @Title: nextDay
     * @Description: 获取传入时间的后一天的开始时刻
     * @author: 李朋
     * @date 2016年5月11日 上午10:33:35
     */
    public static String timeFormat(String time, String format, String format1) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);//格式化为年月
        SimpleDateFormat sdf1 = new SimpleDateFormat(format1);
        return sdf1.format(sdf.parse(time));
    }

    /**
     * @param @param  startTime
     * @param @param  endTime
     * @param @return 设定文件
     * @return List<Map < String, String>> 返回类型
     * Map<String,String>：
     * map.put("startTime", startTime);（value Date类型）
     * map.put("endTime", endTime);（value Date类型）
     * map.put("dateToNumber", dateToNumber);（value String类型）
     * @throws
     * @Title: getSomeDayDate
     * @Description: 时间切割类 给定时间段切割按天切割
     * @author: 刘如意
     * @date 2016年3月18日 上午10:18:06
     */
    public static List<Map<String, String>> getSomeDayDate(String startTime, String endTime) {
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        try {
            int between = daysBetween(startTime, endTime);
            for (int i = 0; i < between; i++) {
                Map<String, String> map = new HashMap<String, String>();
                if (i + 1 == between) {
                    map.put("startTime", startTime);
                    map.put("endTime", endTime);
                    map.put("dateToNumber", dateToNumberDay(startTime));
                    list.add(map);
                } else {
                    map.put("startTime", startTime);
                    map.put("endTime", sameDay(startTime));
                    map.put("dateToNumber", dateToNumberDay(startTime));
                    startTime = nextDay(startTime);
                    list.add(map);
                    //System.out.println(map.toString());
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * @param smdate 小的时间,string格式
     * @param bdate  大的时间,String格式
     * @return int 返回类型
     * @throws ParseException 设定文件
     * @throws
     * @Title: daysBetween
     * @Description: 算出给定两个时间之间的横跨天数，包括这两天
     * @author: 王凯强
     * @date 2016年4月7日 上午10:03:39
     */
    public static int daysBetween(String smdate, String bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT);
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (DateUtil.DAY_TO_MILLIS);

        return Integer.parseInt(String.valueOf(between_days)) + 1;
    }

    /**
     * @param @param  minDate
     * @param @param  maxDate
     * @param @return
     * @param @throws ParseException    设定文件
     * @return List<String>    返回类型
     * @throws
     * @Title: getMonthBetween
     * @Description: 算出给定两个时间之间的横跨月份，包括这两个月
     * @author: 高晓龙
     * @date 2019年4月11日 下午4:10:23
     */
    public static List<String> getMonthBetween(String minDate, String maxDate) throws ParseException {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMM");
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf1.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }
        return result;
    }


    /**
     * @param smdate 小的时间
     * @param bdate  大的时间
     * @return int 返回类型
     * @throws ParseException 设定文件
     * @throws
     * @Title: daysBetween
     * @Description: 算出给定两个时间之间的横跨天数，包括这两天
     * @author: 王凯强
     * @date 2016年4月7日 上午10:03:39
     */
    public static int daysBetween(Date smdate, Date bdate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT);
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (DateUtil.DAY_TO_MILLIS);

        return Integer.parseInt(String.valueOf(between_days)) + 1;
    }

    /**
     * @param @param date
     * @return String 返回类型
     * @throws
     * @Title: dateToNumberDay
     * @Description: 获取传入时间的时间戳
     * @author: 刘如意
     * @date 2016年3月15日 上午11:21:40
     */
    public static String dateToNumberDay(String date) {
        Calendar c = Calendar.getInstance();
        Date dateBegin = new Date();
        String RemarkStr = "";
        try {
            dateBegin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATETIME_FORMAT_SEC);
            c.setTime(dateBegin);
            String month = "";
            String day = "";
            if (c.get(Calendar.MONTH) < 9) {
                month = 1 + c.get(Calendar.MONTH) + "";
                month = "0" + month;
            } else {
                month = 1 + c.get(Calendar.MONTH) + "";
            }
            if (c.get(Calendar.DAY_OF_MONTH) < 10) {
                day = "0" + c.get(Calendar.DAY_OF_MONTH);
            } else {
                day = "" + c.get(Calendar.DAY_OF_MONTH);
            }
            RemarkStr = c.get(Calendar.YEAR) + "" + month + "" + day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return RemarkStr;
    }

    /**
     * @param @param  date
     * @param @return
     * @param @throws ParseException    设定文件
     * @return Date    返回类型
     * @throws
     * @Title: nextDay
     * @Description: 获取传入时间的后一天的开始时刻
     * @author: 刘如意
     * @date 2016年5月11日 上午10:33:35
     */
    public static Date nextDay(Date date) throws ParseException {
        Calendar c = Calendar.getInstance();
        Date dateBegin = new Date();
        String RemarkStr = "";
        dateBegin = date;
        c.setTime(dateBegin);
        String month = "";
        String day = "";
        if (c.get(Calendar.MONTH) < 10) {
            month = 1 + c.get(Calendar.MONTH) + "";
            month = "0" + month;
        } else {
            month = 1 + c.get(Calendar.MONTH) + "";
        }
        if (c.get(Calendar.DAY_OF_MONTH) < 9) {
            day = c.get(Calendar.DAY_OF_MONTH) + "";
            day = "0" + day;
        } else {
            day = c.get(Calendar.DAY_OF_MONTH) + "";
        }
        RemarkStr = c.get(Calendar.YEAR) + "-" + month + "-" + day + " 00:00:00";

        return DateUtil.parseDate(RemarkStr, DEFAULT_DATETIME_FORMAT_SEC);
    }

    public static String nextDay(String date) {
        Calendar c = Calendar.getInstance();
        Date dateBegin = new Date();
        String RemarkStr = "";
        try {
            dateBegin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATETIME_FORMAT_SEC);
            c.setTime(dateBegin);
            c.add(Calendar.DATE, 1);
            String month = "";
            String day = "";
            if (c.get(Calendar.MONTH) < 10) {
                month = 1 + c.get(Calendar.MONTH) + "";
                month = "0" + month;
            } else {
                month = 1 + c.get(Calendar.MONTH) + "";
            }
            if (c.get(Calendar.DAY_OF_MONTH) < 9) {
                day = c.get(Calendar.DAY_OF_MONTH) + "";
                day = "0" + day;
            } else {
                day = c.get(Calendar.DAY_OF_MONTH) + "";
            }
            RemarkStr = c.get(Calendar.YEAR) + "-" + month + "-" + day + " 00:00:00";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return RemarkStr;
    }

    public static String prevDay(String date) {
        Calendar c = Calendar.getInstance();
        Date dateBegin = new Date();
        String RemarkStr = "";
        try {
            dateBegin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATETIME_FORMAT_SEC);
            c.setTime(dateBegin);
            c.add(Calendar.DATE, -1);
            String month = "";
            String day = "";
            if (c.get(Calendar.MONTH) < 10) {
                month = 1 + c.get(Calendar.MONTH) + "";
                month = "0" + month;
            } else {
                month = 1 + c.get(Calendar.MONTH) + "";
            }
            if (c.get(Calendar.DAY_OF_MONTH) < 9) {
                day = c.get(Calendar.DAY_OF_MONTH) + "";
                day = "0" + day;
            } else {
                day = c.get(Calendar.DAY_OF_MONTH) + "";
            }
            RemarkStr = c.get(Calendar.YEAR) + "-" + month + "-" + day + " 00:00:00";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return RemarkStr;
    }

    /**
     * @param @param date
     * @return String 返回类型
     * @throws ParseException
     * @Title: sameDay
     * @Description: 获取传入时间当天最后一个时刻
     * @author: 刘如意
     * @date 2016年3月15日 上午10:33:00 @throws
     */
    public static String sameDay(String date) {
        Calendar c = Calendar.getInstance();
        Date dateBegin = new Date();
        String RemarkStr = "";
        try {
            dateBegin = DateUtil.parseDate(date, DateUtil.DEFAULT_DATETIME_FORMAT_SEC);
            c.setTime(dateBegin);
            c.add(Calendar.DATE, 1);
            String month = "";
            String day = "";
            if (c.get(Calendar.MONTH) < 9) {
                month = 1 + c.get(Calendar.MONTH) + "";
                month = "0" + month;
            } else {
                month = 1 + c.get(Calendar.MONTH) + "";
            }
            if (c.get(Calendar.DAY_OF_MONTH) < 10) {
                day = "0" + c.get(Calendar.DAY_OF_MONTH);
            } else {
                day = "" + c.get(Calendar.DAY_OF_MONTH);
            }
            RemarkStr = c.get(Calendar.YEAR) + "-" + month + "-" + day + " 00:00:00";

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return RemarkStr;
    }

    /**
     * 取当前日期的字符串表示
     *
     * @return 当前日期的字符串 ,如2010-05-28
     **/
    public static String today() {
        return today(DEFAULT_DATE_FORMAT);
    }

    /**
     * 根据输入的格式得到当前日期的字符串
     *
     * @param strFormat 日期格式
     * @return
     */
    public static String today(String strFormat) {
        return toString(new Date(), strFormat);
    }

    /**
     * 取当前时间的字符串表示,
     *
     * @return 当前时间, 如:21:10:12
     **/
    public static String currentTime() {
        return currentTime(DEFAULT_TIME_FORMAT);
    }

    /**
     * 根据输入的格式获取时间的字符串表示
     *
     * @return 当前时间, 如:21:10:12
     **/

    public static String currentTime(String strFormat) {
        return toString(new Date(), strFormat);
    }

    /**
     * @return long 时间戳
     * @throws
     * @Title: getTime
     * @Description: 根据时间格式和时间字符串转换成时间戳
     * @author: 温泉
     * @date 2012-04-19 17:29:06 +0800
     */
    public static long getTime(String time, String strFormat) {
        long result = 0;
        if (!StringUtils.isEmpty(time)) {
            SimpleDateFormat sdf = new SimpleDateFormat(strFormat);
            Date d = null;
            try {
                d = sdf.parse(time);
                result = d.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * @return String 时间格式字符串
     * @Title: getStrTime
     * @Description: 讲时间戳转换成字符串
     * @author: 温泉
     * @date 2012-04-19 17:37:28 +0800
     */
    public static String getStrTime(long datetime, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(datetime));
    }

    /**
     * 取得相对于当前时间增加天数/月数/年数后的日期 <br>
     * 欲取得当前日期5天前的日期,可做如下调用:<br>
     * getAddDay("DATE", -5).
     *
     * @param field  段,如"year","month","date",对大小写不敏感
     * @param amount 增加的数量(减少用负数表示),如5,-1
     * @return 格式化后的字符串 如"2010-05-28"
     * @throws ParseException
     **/

    public static String getAddDay(String field, int amount) throws ParseException {
        return getAddDay(field, amount, null);
    }

    /**
     * 取得相对于当前时间增加天数/月数/年数后的日期,按指定格式输出
     * <p>
     * 欲取得当前日期5天前的日期,可做如下调用:<br>
     * getAddDay("DATE", -5,'yyyy-mm-dd hh:mm').
     *
     * @param field     段,如"year","month","date",对大小写不敏感
     * @param amount    增加的数量(减少用负数表示),如5,-1
     * @param strFormat 输出格式,如"yyyy-mm-dd","yyyy-mm-dd hh:mm"
     * @return 格式化后的字符串 如"2010-05-28"
     * @throws ParseException
     **/
    public static String getAddDay(String field, int amount, String strFormat) throws ParseException {
        return getAddDay(null, field, amount, strFormat);
    }

    /**
     * 功能：对于给定的时间增加天数/月数/年数后的日期,按指定格式输出
     *
     * @param date      String 要改变的日期
     * @param field     int 日期改变的字段，YEAR,MONTH,DAY
     * @param amount    int 改变量
     * @param strFormat 日期返回格式
     * @return
     * @throws ParseException
     */
    public static String getAddDay(String date, String field, int amount, String strFormat) throws ParseException {
        if (strFormat == null) {
            strFormat = DEFAULT_DATETIME_FORMAT_SEC;
        }
        Calendar rightNow = Calendar.getInstance();
        if (date != null && !"".equals(date.trim())) {
            rightNow.setTime(parseDate(date, strFormat));
        }
        if (field == null) {
            return toString(rightNow.getTime(), strFormat);
        }
        rightNow.add(getInterval(field), amount);
        return toString(rightNow.getTime(), strFormat);
    }

    /**
     * 获取时间间隔类型
     *
     * @param field 时间间隔类型
     * @return 日历的时间间隔
     */
    protected static int getInterval(String field) {
        String tmpField = field.toUpperCase();
        if (tmpField.equals(DEFAULT_YEAR)) {
            return Calendar.YEAR;
        } else if (tmpField.equals(DEFAULT_MONTH)) {
            return Calendar.MONTH;
        } else if (tmpField.equals(DEFAULT_DATE)) {
            return Calendar.DATE;
        } else if (DEFAULT_HOUR.equals(tmpField)) {
            return Calendar.HOUR;
        } else if (DEFAULT_MINUTE.equals(tmpField)) {
            return Calendar.MINUTE;
        } else {
            return Calendar.SECOND;
        }
    }

    /**
     * 获取格式化对象
     *
     * @param strFormat 格式化的格式 如"yyyy-MM-dd"
     * @return 格式化对象
     */
    public static SimpleDateFormat getSimpleDateFormat(String strFormat) {
        if (strFormat != null && !"".equals(strFormat.trim())) {
            return new SimpleDateFormat(strFormat);
        } else {
            return new SimpleDateFormat();
        }
    }

    /**
     * 得到当前日期的星期数
     *
     * @return 当前日期的星期的字符串
     * @throws ParseException
     */
    public static String getWeekOfMonth() throws ParseException {
        return getWeekOfMonth(null, null);
    }

    /**
     * 根据日期的到给定日期的在当月中的星期数
     *
     * @param date 给定日期
     * @return
     * @throws ParseException
     */
    public static String getWeekOfMonth(String date, String fromat) throws ParseException {
        Calendar rightNow = Calendar.getInstance();
        if (date != null && !"".equals(date.trim())) {
            rightNow.setTime(parseDate(date, fromat));
        }
        return WEEKS[rightNow.get(Calendar.WEEK_OF_MONTH)];
    }

    /**
     * 将java.util.date型按照指定格式转为字符串
     *
     * @param date   源对象
     * @param format 想得到的格式字符串
     * @return 如：2010-05-28
     */
    public static String toString(Date date, String format) {
        return getSimpleDateFormat(format).format(date);
    }

    /**
     * 将java.util.date型按照缺省格式转为字符串
     *
     * @param date 源对象
     * @return 如：2010-05-28
     */
    public static String toString(Date date) {
        return toString(date, DEFAULT_DATE_FORMAT);
    }

    /**
     * 强制类型转换 从串到日期
     *
     * @return 得到的日期对象
     * @throws ParseException
     */
    public static Date parseDate(String strDate, String format) throws ParseException {
        return getSimpleDateFormat(format).parse(strDate);
    }

    /***
     * 根据传入的毫秒数和格式，对日期进行格式化输出
     *
     * @version 2011-7-12
     * @param format
     * @return
     */
    public static String millisecondFormat(Long millisecond, String format) {
        if (millisecond == null || millisecond <= 0) {
            throw new IllegalArgumentException(String.format("传入的时间毫秒数[%s]不合法", "" + millisecond));
        }
        if (format == null || "".equals(format.trim())) {
            format = DEFAULT_DATE_FORMAT;
        }
        return toString(new Date(millisecond), format);
    }

    /**
     * 强制类型转换 从串到时间戳
     *
     * @return 取得的时间戳对象
     * @throws ParseException
     */
    public static Timestamp parseTimestamp(String strDate, String format) throws ParseException {
        Date utildate = getSimpleDateFormat(format).parse(strDate);
        return new Timestamp(utildate.getTime());
    }

    /**
     * getCurDate 取当前日期
     *
     * @return java.util.Date型日期
     **/
    public static Date getCurDate() {
        return (new Date());
    }

    /**
     * getCurTimestamp 取当前时间戳
     *
     * @return java.sql.Timestamp
     **/
    public static Timestamp getCurTimestamp() {
        return new Timestamp(new Date().getTime());
    }

    public static Timestamp getTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    /**
     * 获得前多少分钟的时间点
     *
     * @param minute
     * @param time
     * @return
     */
    public static String getAfterMinute(int minute, String time) {
        Date date = new Date();
        try {
            date = parseDate(time, DateUtil.DEFAULT_DATETIME_FORMAT_SEC);
        } catch (ParseException e) {
            e.printStackTrace();
        }// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minute);
        date = calendar.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(DateUtil.DEFAULT_DATETIME_FORMAT_SEC);
        return formatter.format(date);
    }

    public static String getAfterDay(int day) {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, day);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public static String getAfterMonth(int month) {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, month);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    public static Map<String, String> getLastWeek() {
        Map<String, String> map = new HashMap<String, String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -7);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        String monday = format.format(calendar.getTime());
        calendar.add(Calendar.DATE, 7);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        String sunday = format.format(calendar.getTime());

        map.put("monday", monday);
        map.put("sunday", sunday);
        return map;
    }

    public static Map<String, String> getLastMonth() {
        Map<String, String> map = new HashMap<String, String>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firtDay = format.format(calendar.getTime());

        //得到一个月最后一天日期(31/30/29/28)
        int MaxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        //按你的要求设置时间
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), MaxDay);
        //按格式输出
        String lastDay = format.format(calendar.getTime()); //上月最后一天

        map.put("firtDay", firtDay);
        map.put("lastDay", lastDay);
        return map;
    }

    public static String getAfterYear(int year) {
        Date date = new Date();// 取时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, year);// 把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime(); // 这个时间就是日期往后推一天的结果
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }

    /**
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: getTimesmorning
     * @Description: 获取当天00:00:00
     * @author: 高晓龙
     * @date 2018年11月27日 下午4:14:22
     */
    public static String getStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        Date beginOfDate = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(beginOfDate);
    }

    /**
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: getEndTime
     * @Description: 获取当天23:59:59
     * @author: 高晓龙
     * @date 2018年11月27日 下午4:14:33
     */
    public static String getEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        Date beginOfDate = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(beginOfDate);
    }

    public static String getDayOfWeek() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return formatter.format(calendar.getTime());
    }

    public static String getDayOfMonth() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return format.format(calendar.getTime());
    }

    /**
     * getCurTimestamp 取遵循格式的当前时间
     *
     * @return java.sql.Timestamp
     **/
    public static Date getCurDate(String format) throws Exception {
        return getSimpleDateFormat(format).parse(toString(new Date(), format));
    }

    public static String getDate(String format) {
        return getSimpleDateFormat(format).format(new Date());
    }

    /**
     * Timestamp按照指定格式转为字符串
     *
     * @param timestamp 源对象
     * @param format    ps（如yyyy.mm.dd）
     * @return 如：2010-05-28 或2010-05-281 13:21
     */
    public static String toString(Timestamp timestamp, String format) {
        if (timestamp == null) {
            return "";
        }
        return toString(new Date(timestamp.getTime()), format);
    }

    /**
     * Timestamp按照缺省格式转为字符串
     *
     * @param ts 源对象
     * @return 如：2010-05-28
     */
    public static String toString(Timestamp ts) {
        return toString(ts, DEFAULT_DATE_FORMAT);
    }

    /**
     * Timestamp按照缺省格式转为字符串，可指定是否使用长格式
     *
     * @param timestamp  欲转化之变量Timestamp
     * @param fullFormat 是否使用长格式
     * @return 如：2010-05-28 或2010-05-28 21:21
     */
    public static String toString(Timestamp timestamp, boolean fullFormat) {
        if (fullFormat) {
            return toString(timestamp, DEFAULT_DATETIME_FORMAT_SEC);
        } else {
            return toString(timestamp, DEFAULT_DATE_FORMAT);
        }
    }

    /**
     * 将sqldate型按照指定格式转为字符串
     *
     * @param sqldate 源对象
     * @param sFormat ps
     * @return 如：2010-05-28 或2010-05-28 00:00
     */
    public static String toString(java.sql.Date sqldate, String sFormat) {
        if (sqldate == null) {
            return "";
        }
        return toString(new Date(sqldate.getTime()), sFormat);
    }

    /**
     * 将sqldate型按照缺省格式转为字符串
     *
     * @param sqldate 源对象
     * @return 如：2010-05-28
     */
    public static String toString(java.sql.Date sqldate) {
        return toString(sqldate, DEFAULT_DATE_FORMAT);
    }

    /**
     * 计算日期时间之间的差值， date1得时间必须大于date2的时间
     *
     * @param date1
     * @param date2
     * @return {@link Map} Map的键分别为, day(天),
     * hour(小时),minute(分钟)和second(秒)。
     * @version 2011-7-12
     */
    public static Map<String, Long> timeDifference(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            throw new NullPointerException("date1 and date2 can't null");
        }
        long mim1 = date1.getTime();
        long mim2 = date2.getTime();
        if (mim1 < mim2) {
            throw new IllegalArgumentException(String.format("date1[%s] not be less than date2[%s].", mim1 + "", mim2 + ""));
        }
        long m = (mim1 - mim2 + 1) / 1000l;
        long mday = 24 * 3600;
        final Map<String, Long> map = new HashMap<String, Long>();
        map.put("day", m / mday);
        m = m % mday;
        map.put("hour", (m) / 3600);
        map.put("minute", (m % 3600) / 60);
        map.put("second", (m % 3600 % 60));
        return map;
    }

    public static Map<String, Integer> compareTo(final Date date1, final Date date2) {
        if (date1 == null || date2 == null) {
            return null;
        }
        long time1 = date1.getTime();
        long time2 = date2.getTime();
        long time = Math.max(time1, time2) - Math.min(time1, time2);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("year", (calendar.get(Calendar.YEAR) - 1970) > 0 ? (calendar.get(Calendar.YEAR) - 1970) : 0);
        map.put("month", (calendar.get(Calendar.MONTH) - 1) > 0 ? (calendar.get(Calendar.MONTH) - 1) : 0);
        map.put("day", (calendar.get(Calendar.DAY_OF_MONTH) - 1) > 0 ? (calendar.get(Calendar.DAY_OF_MONTH) - 1) : 0);
        map.put("hour", (calendar.get(Calendar.HOUR_OF_DAY) - 8) > 0 ? (calendar.get(Calendar.HOUR_OF_DAY) - 8) : 0);
        map.put("minute", calendar.get(Calendar.MINUTE) > 0 ? calendar.get(Calendar.MINUTE) : 0);
        map.put("second", calendar.get(Calendar.SECOND) > 0 ? calendar.get(Calendar.SECOND) : 0);
        return map;
    }

    public static String getLastDayOfMonth(String year, String month) {
        Calendar cal = Calendar.getInstance();
        // 年
        cal.set(Calendar.YEAR, Integer.parseInt(year));
        // 月，因为Calendar里的月是从0开始，所以要-1
        cal.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        // 日，设为一号
        cal.set(Calendar.DATE, 1);
        // 月份加一，得到下个月的一号
        cal.add(Calendar.MONTH, 1);
        // 下一个月减一为本月最后一天
        cal.add(Calendar.DATE, -1);
        return String.valueOf(cal.get(Calendar.DAY_OF_MONTH));// 获得月末是几号
    }

    /**
     * @param @param  d
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: toXmlDateTime
     * @Description: java
     * 的日期字符串描述：http://docs.oracle.com/javase/tutorial/i18n/format
     * /simpleDateFormat.html<br />
     * XML的日期字符串描述：http://www.w3school.com.cn/schema/
     * schema_dtypes_date.asp<br />
     * 由于java和xml的时区无法兼容，只能手动写上'+08:00'
     * @author: 韩欣宇
     * @date 2014-04-06 17:51:32
     * +0800
     */
    public static String toXmlDateTime(Date d) {
        // 2014-03-24T07:43:18Z
        return toString(d, "yyyy-MM-dd'T'HH:mm:ss'+08:00'");
    }

    /**
     * @param @param  beginDate
     * @param @param  endDate
     * @param @return 设定文件
     * @return List<Date> 返回类型
     * @throws
     * @Title: getDatesBetweenAndDate
     * @Description: 获取年月日范围集合
     * @author: 阮启伟
     * @date 2016年3月31日 下午3:18:26
     */
    public static List<Date> getDatesBetweenAndDate(Date beginDate, Date endDate) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(beginDate);// 把开始时间加入集合
        Calendar cal = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        cal.setTime(beginDate);
        boolean bContinue = true;
        while (bContinue) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cal.add(Calendar.DAY_OF_MONTH, 1);
            // 测试此日期是否在指定日期之后
            if (endDate.after(cal.getTime())) {
                lDate.add(cal.getTime());
            } else {
                break;
            }
        }
        lDate.add(endDate);// 把结束时间加入集合
        return lDate;
    }

    /**
     * @param @param  date
     * @param @param  s
     * @param @return 设定文件
     * @return Date 返回类型
     * @throws
     * @Title: addMonth
     * @Description: 时间加减月
     * @author: 刘如意
     * @date 2016年4月19日 下午5:46:22
     */
    public static Date addMonth(Date date, int s) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, s);// 24小时制
        date = cal.getTime();
        cal = null;
        return date;
    }

    /**
     * @param @param  dateJudge
     * @param @param  dateOne
     * @param @param  dateTwo
     * @param @return 设定文件
     * @return boolean 返回类型
     * @throws
     * @Title: bettonInDate
     * @Description: 判断第一个时间是否在后两个时间中间
     * @author: 刘如意
     * @date 2016年4月19日 下午5:47:09
     */
    public static boolean betweenInDate(Date dateJudge, Date dateOne, Date dateTwo) {
        boolean bool = false;
        if ((dateJudge.getTime() <= dateOne.getTime() && dateJudge.getTime() >= dateTwo.getTime()) || (dateJudge.getTime() <= dateTwo.getTime() && dateJudge.getTime() >= dateOne.getTime())) {
            bool = true;
        }
        return bool;
    }

    /**
     * 此方法是秒级别的
     *
     * @param @param  longTime
     * @param @return 设定文件
     * @return String 返回类型
     * @throws
     * @Title: formatLongToTimeStr
     * @Description: 将Integer型的时间转为小时分钟秒字符
     * @author: 孟祥瑞
     * @date 2016年4月25日 下午2:16:39
     */
    public static String formatIntegerToTimeStr(Integer time) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        second = time.intValue();
        if (second > 60) {
            minute = second / 60;
            second = second % 60;
        }

        if (minute > 60) {
            hour = minute / 60;
            minute = minute % 60;
        }
        String strtime = hour + "小时" + minute + "分钟" + second + "秒";
        return strtime;
    }

    /**
     * @param @param  date
     * @param @param  s
     * @param @return 设定文件
     * @return Date 返回类型
     * @throws
     * @Title: addDate
     * @Description: 给时间加上天
     * @author: 刘如意
     * @date 2016年4月19日 下午5:47:38
     */
    public static Date addDate(Date date, int s) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, s);// 24小时制
        date = cal.getTime();
        cal = null;
        return date;
    }

    public static String timeDifferenceInfo(Date date, Date date2) {
        long diff = date2.getTime() - date.getTime();
        if (diff > 0) {
            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
            long ss = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
            return "" + days + "天" + hours + "小时" + minutes + "分" + ss + "秒";
        } else {
            return null;
        }
    }

    /**
     * @param year
     * @param month
     * @param day
     * @return List<Date>    返回类型
     * @throws
     * @Title: getTimeSegment
     * @Description: TODO(取值每隔一小时的时间 00 : 59 01 : 59)
     * @author: zhzy
     * @date 2017年2月21日 下午1:55:14
     */
    public static List<Date> getTimeSegmentoo(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 59);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();
        cal.set(year, month - 1, day, 23, 59);
        long endTime = cal.getTimeInMillis();
        final int seg = 60 * 60 * 1000;//取值间隔60分钟的值
        ArrayList<Date> result = new ArrayList<Date>((int) ((endTime - startTime) / seg + 1));
        for (long time = startTime; time <= endTime; time += seg) {
            result.add(new Date(time));
        }
        return result;
    }

    /**
     * @param year
     * @param month
     * @param day
     * @return List<Date>    返回类型
     * @throws
     * @Title: getTimeSegmentfiftynine
     * @Description: TODO(取值每隔一小时的时间 00 : 00 01 : 00)
     * @author: zhzy
     * @date 2017年2月21日 下午2:12:52
     */
    public static List<Date> getTimeSegmentfiftynine(int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, day, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long startTime = cal.getTimeInMillis();
        cal.set(year, month - 1, day, 23, 59);
        long endTime = cal.getTimeInMillis();
        final int seg = 60 * 60 * 1000;//取值间隔60分钟的值
        ArrayList<Date> result = new ArrayList<Date>((int) ((endTime - startTime) / seg + 1));
        for (long time = startTime; time <= endTime; time += seg) {
            result.add(new Date(time));
        }
        return result;
    }

    /**
     * @param number
     * @return String    返回类型
     * @throws
     * @Title: getStatetime
     * @Description: TODO(计算当前日期前几日日期天数)
     * @author: zhzy
     * @date 2017年2月20日 下午4:09:28
     */
    public static String getStatetime(int number) {

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -number);
        Date monday = c.getTime();
        String time = DateUtil.toString(monday, DateUtil.DEFAULT_DATE_FORMAT);
        return time;
    }

    /**
     * 把传入的时间往后推年限
     *
     * @param date 当前日期
     * @param life 年限
     * @return
     * @author: 雷鹏
     * @dete 2017年9月19日12:55:05
     */
    public static Date getEndDate(Date date, double life) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int integer = (int) life;
        if (integer > 0) {
            cal.add(Calendar.YEAR, integer);
        }
        int day = (int) ((life - integer) * 365);
        if (day > 0) {
            cal.add(Calendar.DATE, day);
        }
        Date endDate = cal.getTime();
        return endDate;
    }

    /**
     * @param @param  time
     * @param @param  num
     * @param @return 设定文件
     * @return String    返回类型
     * @throws
     * @Title: addDay
     * @Description: 字符串时间加天
     * @author: 雷鹏
     * @date 2017年9月30日 下午4:17:52
     */
    public static String addDay(String time, int num) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATETIME_FORMAT_SEC);
        try {
            Date date = dateFormat.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DAY_OF_MONTH, num);// 24小时制
            date = cal.getTime();
            cal = null;
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String addMinute(String time, int num) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATETIME_FORMAT_SEC);
        try {
            Date date = dateFormat.parse(time);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.MINUTE, num);// 24小时制
            date = cal.getTime();
            cal = null;
            return dateFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static byte dectoBCD(int int10) {
        int10 = int10 % 100;
        return (byte) (((int10 / 10) << 4) + ((int10 % 10) & 0x0F));
    }

    @SuppressWarnings("deprecation")
    public static byte[] getBCDDate(Date d) {
        return new byte[]{dectoBCD(d.getYear() % 100), dectoBCD(d.getMonth() + 1), dectoBCD(d.getDate())};
    }

    public static String getShipmentTable(String date, int formet) {
        String shipmentTable = "";
        try {
            date = DateUtil.getAddDay(date, "month", formet,
                    DateUtil.DEFAULT_DATE_FORMAT);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            shipmentTable = "T_SHIPMENTFINISHED_"
                    + DateUtil.toString(sqlDate, "yyyyMM");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return shipmentTable;
    }

    public static String updateHourToString(String time, int hour) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = sdf.parse(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse);
        cal.add(Calendar.HOUR, hour);
        parse = cal.getTime();
        return sdf.format(parse);
    }

    public static String updateHourString(String time, int hour) throws ParseException {
        SimpleDateFormat sdf = null;
        Date parse = null;
        SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            parse = sdf.parse(time);
        } catch (Exception e) {
            sdf = new SimpleDateFormat("yyyy-MM-dd");
            parse = sdf.parse(time);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse);
        cal.add(Calendar.HOUR, hour);
        parse = cal.getTime();
        return sdfs.format(parse);
    }

    public static Date updateHour(String time, int hour) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date parse = sdf.parse(time);
        Calendar cal = Calendar.getInstance();
        cal.setTime(parse);
        cal.add(Calendar.HOUR, hour);
        parse = cal.getTime();
        return parse;
    }

    public static Date updateHour(Date time, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.HOUR, hour);
        time = cal.getTime();
        return time;
    }

    /**
     * 获取本月开始日期
     *
     * @return String
     **/
    public static String getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(time) + " 00:00:00";
    }

    /**
     * 获取今天
     *
     * @return String
     */
    public static String getToday() {
        return new SimpleDateFormat(DEFAULT_DATETIME_FORMAT_SEC).format(new Date());
    }

    /**
     * 上月开始时间
     *
     * @return String
     */
    public static String getLastMonthStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date time = cal.getTime();
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(time) + " 00:00:00";
    }

    /**
     * 上月开始结束时间
     *
     * @return String
     */
    public static String getLastMonthEndTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(Calendar.DATE, -1);
        Date time = calendar.getTime();
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(time) + " 00:00:00";
    }

    /**
     * 获取前几个月的今天
     *
     * @return String
     */
    public static String getFewMonthsTime(int months) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -0);    //得到今天
        calendar.add(Calendar.MONTH, months);    //得到前3个月
        Date time = calendar.getTime();
        return new SimpleDateFormat(DEFAULT_DATE_FORMAT).format(time) + " 00:00:00";
    }

    /***
     * 获取某一时间 所在 月份 下个月的第一天
     *  比如 ： 2020-01-20
     *     结果为：2020-02-01 00:00:00
     * */
    public static Date getFirstDayOfNext(Date date) {
        Calendar nextMonthFirst = Calendar.getInstance();
        nextMonthFirst.setTime(date);
        nextMonthFirst.set(Calendar.DAY_OF_MONTH, 1);
        nextMonthFirst.add(Calendar.MONTH, 1);
        return nextMonthFirst.getTime();
    }

//    public static void main(String[] args) throws ParseException {
//    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
//    	SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd");
//    	System.out.println(dft.format(getFirstDayOfNext(sdf.parse("2021-11"))));
//	}


    public static String formatDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.NORM_DATETIME_FORMAT.format(date);
    }

    public static String now() {
        return formatDateTime(new DateTime());
    }

    /**
     * 格式化时间
     *
     * @param time
     * @return
     */
    public static String formatDate(String time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (time == null || "".equals(time)) {
            return "";
        }
        Date date = null;
        try {
            date = format.parse(time);
            time = format.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        if (current.after(today)) {
            return "今天 " + time.split(" ")[1];
        } else if (current.before(today) && current.after(yesterday)) {

            return "昨天 " + time.split(" ")[1];
        } else {
            int index = time.indexOf("-") + 1;
            return format.format(date);
        }
    }

    public static boolean verifyTimeFormat(String time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            sdf.parse(time);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 根据传入的分钟转化为对应的天数小时分钟 如 : 1天12小时23分
     *
     * @param m 分钟
     * @return
     */
    public static String getNumTime(String m) {
        long minute = Long.parseLong(m);
        String days = "";
        if (minute < 60) {
            days = String.valueOf(minute) + "分钟";
        } else if (minute < 1440) {
            long value = minute % 60;
            days = String.valueOf((minute - value) / 60) + "小时"
                    + String.valueOf(value) + "分钟";
        } else {
            long MinuteValue = minute % 60;
            long value = (minute - MinuteValue) / 60;
            long hourValue = value % 24;
            long dayValue = (value - hourValue) / 24;
            days = String.valueOf(dayValue) + "天" + String.valueOf(hourValue) + "小时"
                    + MinuteValue + "分钟";
        }
        return days;
    }

    public static void main(String[] args) {
        System.out.println(getNumTime("1452"));
    }

}
