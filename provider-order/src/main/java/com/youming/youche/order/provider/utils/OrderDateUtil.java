package com.youming.youche.order.provider.utils;

import com.youming.youche.order.util.OrderUtil;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

import static com.youming.youche.order.provider.service.CreditRatingRuleServiceImpl.getMillis;

@Component
public class OrderDateUtil {


    /**
     * 开始时间+多少个小时 的时间
     * @param orig  开始时间
     * @param incrFloat 单位小时
     * @return
     *    返回添加后的时间
     */
    public LocalDateTime addHourAndMins(LocalDateTime orig, Float incrFloat){
        float incr = incrFloat == null ? 0f : incrFloat;
        int incrHour = (int)incr;
        int incrMins = incrHour*60;
//        int incrMins = new BigDecimal(incr).subtract(new BigDecimal(incrHour)).multiply(new BigDecimal(60)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        LocalDateTime localDateTime = orig.plusMinutes(incrMins);
        return localDateTime;
    }

    /**
     * 开始时间-多少个小时 的时间
     * @param orig  开始时间
     * @param incrFloat 单位小时
     * @return
     *    返回减后的时间
     */
    public static LocalDateTime subHourAndMins(LocalDateTime orig, Float incrFloat){
        float incr = incrFloat == null ? 0f : incrFloat;
        int incrHour = (int)incr;
        int incrMins = incrHour*60;
//        int incrMins = new BigDecimal(incr).subtract(new BigDecimal(incrHour)).multiply(new BigDecimal(60)).setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
        LocalDateTime localDateTime = orig.minusMinutes(incrMins);
        return localDateTime;
    }

    public static Date formatStringToDate(String str, String format) throws ParseException {
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        date = sdf.parse(str);
        return date;
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

    /**
     * 两个时间相差多少天
     *
     * @param date1
     * @param date2 eg:2017-06-04 00:00:00    2017-06-06 09:00:00 返回3天
     *              eg:2017-06-04 00:00:00    2017-06-04 09:00:00 返回1天
     * @throws ParseException
     */
    public static int getDifferDay(Date date1, Date date2) {
        Calendar bef = Calendar.getInstance();
        Calendar aft = Calendar.getInstance();
        bef.setTime(OrderDateUtil.parseDate(OrderUtil.formatDate(date1, "yyyy-MM-dd 00:00:00")));
        aft.setTime(OrderDateUtil.parseDate(OrderUtil.formatDate(date2, "yyyy-MM-dd 00:00:00")));
        long one = Math.abs((aft.getTime().getTime() / 1000 - bef.getTime().getTime() / 1000) / 3600 / 24);
        return Integer.parseInt(one + "");
    }

    /**
     * LocalDateTime 转String
     * @param time
     * @return
     */
    public String stringByLocalDateTime(LocalDateTime time) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String localTimeStr = df.format(time);
        return localTimeStr;
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
