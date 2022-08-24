package com.youming.youche.finance.constant;

import java.util.Calendar;
import java.util.Date;

/**
 * 处理线上打款工具类
 * 聂杰伟
 * 2022-3-10
 */
public class PayOutIntfUtil {

    public static Date getDay(Date date , int diff){
        Calendar cal   =   Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE,   diff);
        return cal.getTime();
    }
}
