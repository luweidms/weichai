package com.youming.youche.util;

import java.text.SimpleDateFormat;
import java.util.*;

public class TimeUtil {

    private static String DEFAULT_FORMAT = "yyyy-MM-dd";


    public static String getSysTime() {

        SimpleDateFormat df = new SimpleDateFormat(DateUtil.DATETIME12_FORMAT);// 设置日期格式
        return df.format(new Date());
    }

    public static String getTime() {
        SimpleDateFormat df = new SimpleDateFormat(DateUtil.YEAR_MONTH_FORMAT2);// 设置日期格式
        return df.format(new Date());
    }

    public static String getTime(String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
        return df.format(new Date());
    }
    public static String getTime(String format,Date time) {
        SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
        return df.format(time);
    }
    public static Date getTime(String format,String time)throws Exception{
        SimpleDateFormat df = new SimpleDateFormat(format);// 设置日期格式
        return df.parse(time);
    }
    public static String getTimeToStr(String format,String time)throws Exception{
        Date date = new SimpleDateFormat(DateUtil.DATETIME12_FORMAT).parse(time);
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 获取当前时间
     * @param format
     * @return
     */
    public static Date getDataTime() {
            return new Date();

    }

    /**
     * 格式化日期
     * @param date 日期对象
     * @return String 日期字符串
     */
    public static String formatDate(Date date){
        SimpleDateFormat f = new SimpleDateFormat(DEFAULT_FORMAT);
        String sDate = f.format(date);
        return sDate;
    }

    /**
     * 获取当年的第一天
     * @param year
     * @return
     */
    public static Date getCurrYearFirst(){
        Calendar currCal=Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearFirst(currentYear);
    }

    /**
     * 获取当年的最后一天
     * @param year
     * @return
     */
    public static Date getCurrYearLast(){
        Calendar currCal=Calendar.getInstance();
        int currentYear = currCal.get(Calendar.YEAR);
        return getYearLast(currentYear);
    }

    /**
     * 获取某年第一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearFirst(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    /**
     * 获取某年最后一天日期
     * @param year 年份
     * @return Date
     */
    public static Date getYearLast(int year){
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        calendar.roll(Calendar.DAY_OF_YEAR, -1);
        Date currYearLast = calendar.getTime();

        return currYearLast;
    }
    /**
     * 获取距离今晚0时的秒数
     */
    public static int getTodaySeconds(){
        Date current = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND,59);
        calendar.set(Calendar.MINUTE,59);
        return Integer.parseInt(""+(calendar.getTimeInMillis()-current.getTime())/1000);
    }

    /**
     * 获取开始和结束时间的月份只限制2月
     * @param minDate
     * @param maxDate
     * @return
     * @throws Exception
     */
    public static Map getMonthBetween(String minDate, String maxDate) throws Exception {
        Map map = new HashMap();
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DATETIME12_FORMAT);//格式化为年月
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(sdf.parse(minDate));
        max.setTime(sdf.parse(maxDate));
        int minYearNum=min.get(Calendar.YEAR);
        int minMonthNum=min.get(Calendar.MONTH);
        int minDateNum=min.get(Calendar.DATE);
        int minHourNum=min.get(Calendar.HOUR_OF_DAY);
        int minMinuteNum=min.get(Calendar.MINUTE);
        int minSecondNum=min.get(Calendar.SECOND);
        int maxYearNum=max.get(Calendar.YEAR);
        int maxMonthNum=max.get(Calendar.MONTH);
        int maxDateNum=max.get(Calendar.DATE);
        int maxHourNum=max.get(Calendar.HOUR_OF_DAY);
        int maxMinuteNum=max.get(Calendar.MINUTE);
        int maxSecondNum=max.get(Calendar.SECOND);
        if(minYearNum!=maxYearNum){
            return map;
        }
        if(minYearNum==maxYearNum && (maxMonthNum-minMonthNum)>1){
            return map;
        }
        if(minYearNum==maxYearNum && minMonthNum==maxMonthNum){
            result.add(sdf.format(min.getTime()));
            result.add(sdf.format(max.getTime()));
            map.put("1", result);
            return map;
        }
        if(minYearNum==maxYearNum && minMonthNum>maxMonthNum){
            return map;
        }
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar curr = min;
        for(int i=0;i<2;i++) {
            if(i==0){
                curr.set(Calendar.DATE, minDateNum);
                curr.set(Calendar.HOUR_OF_DAY, minHourNum);
                curr.set(Calendar.MINUTE, minMinuteNum);
                curr.set(Calendar.SECOND, minSecondNum);
            }else{
                curr.set(Calendar.DATE, maxDateNum);
                curr.set(Calendar.HOUR_OF_DAY, maxHourNum);
                curr.set(Calendar.MINUTE, maxMinuteNum);
                curr.set(Calendar.SECOND, maxSecondNum);
            }
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }
        map.put("2", result);
        return map;
    }
    /**
     * 获取时间段（分钟）
     * @param startDate 起始时间
     * @param endDate 结束时间
     * @return
     */
    public static long getMinsBetween(Date startDate ,Date endDate){
        long startDateMilliseconds = startDate.getTime();
        long endDateMilliseconds = endDate.getTime();
        long mins = (endDateMilliseconds - startDateMilliseconds)/(1000*60);
        return mins;
    }

    public static Date getNextMonthDay(int day){
        Date date = new Date();
        int year = DateUtil.getYear(date);
        int month = DateUtil.getMonth(date);
        month = month +1;
        if (month == 13) {
            year = year +1;
            month = 1;
        }
        String m = month<10?"0"+month:month+"";
        String d = day < 10? "0"+day:day+"";
        Date rtnDate = DateUtil.parseDate(year+"-"+m+"-"+d,DateUtil.DATE_FORMAT);
        return rtnDate;
    }


    public static Date getNextMonthLastDay(){
        Date date = new Date();
        int year = DateUtil.getYear(date);
        int month = DateUtil.getMonth(date);
        month = month +1;
        if (month == 13) {
            year = year +1;
            month = 1;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(year, month,0);
        Date rtnDate = calendar.getTime();
        return rtnDate;
    }

    /**
     * 根据传入时间，获取传入时间下个月的某一天日期
     *
     * @param date
     * @param day
     * @return
     */
    public static Date getNextMonthDate(Date date, int day){
        Calendar calendar = Calendar.getInstance();
        try {
            if (date != null && day > 0) {
                calendar.setTime(date);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 输入指定日期，返回该日期当月第一天、最后一天、上个月第一天和上个月最后一天、上上个月第一天和上上个月最后一天
     * @param args
     * @throws Exception
     */
    public static Map<String, String> returnDate(Date date){
        Map<String,String> map = new HashMap<String,String>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM");
        //获取指定日期上个月的第一天
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, -1);
        ca.set(Calendar.DAY_OF_MONTH,1);
        String lastFirstDay = dateFormat.format(ca.getTime());
        String lastMonth = dateFormat1.format(ca.getTime());
        ////获取指定日期上个月的最后一天
        Calendar ca1 = Calendar.getInstance();
        ca1.setTime(date);
        ca1.set(Calendar.DAY_OF_MONTH,0);
        String lastLastDay = dateFormat.format(ca1.getTime());
        //获取指定日期所在月第一天
        Calendar ca2 = Calendar.getInstance();
        ca2.setTime(date);
        ca2.add(Calendar.MONTH, 0);
        ca2.set(Calendar.DAY_OF_MONTH,1);
        String firstDay = dateFormat.format(ca2.getTime());
        //获取指定日期所在月最后一天
        Calendar ca3 = Calendar.getInstance();
        ca3.setTime(date);
        ca3.set(Calendar.DAY_OF_MONTH, ca3.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = dateFormat.format(ca3.getTime());
        //获取指定日期上上个月的第一天
        Calendar ca4 = Calendar.getInstance();
        ca4.setTime(date);
        ca4.add(Calendar.MONTH, -2);
        ca4.set(Calendar.DAY_OF_MONTH,1);
        String beforeLastFirstDay = dateFormat.format(ca4.getTime());
        //获取指定日期上上个月的最后一天
        Calendar ca5 = Calendar.getInstance();
        ca5.setTime(date);
        ca5.add(Calendar.MONTH, -1);
        ca5.set(Calendar.DAY_OF_MONTH,0);
        String beforeLastLastDay = dateFormat.format(ca5.getTime());
        map.put("lastFirstDay", lastFirstDay);
        map.put("lastLastDay", lastLastDay);
        map.put("firstDay", firstDay);
        map.put("lastDay", lastDay);
        map.put("lastMonth", lastMonth);
        map.put("beforeLastFirstDay", beforeLastFirstDay);
        map.put("beforeLastLastDay", beforeLastLastDay);
        return map;
    }

    public static String getMonthFirstDay(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca=Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, 0);
        ca.set(Calendar.DAY_OF_MONTH,1);
        return dateFormat.format(ca.getTime());
    }

    public static String getMonthLastDay(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, 1);
        ca.set(Calendar.DAY_OF_MONTH,0);
        return dateFormat.format(ca.getTime());
    }
}
