package com.youming.youche.util;



import com.youming.youche.commons.exception.BusinessException;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.lang.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang.NumberUtils.isNumber;


/**
 * Created by yangliu on 2018/4/7.
 */
public class CommonUtils extends BeanUtils {
    public static void copyProperties(Object dest, Object orig, Object compareObj)
            throws IllegalAccessException, InvocationTargetException {
        if(dest==null||orig==null||compareObj==null){
            return;
        }
        BeanUtilsBean instance = BeanUtilsBean.getInstance();
        PropertyDescriptor[] origDescriptors =
                instance.getPropertyUtils().getPropertyDescriptors(orig);
        for (int i = 0; i < origDescriptors.length; i++) {
            String name = origDescriptors[i].getName();
            if ("class".equals(name)) {
                continue; // No point in trying to set an object's class
            }
            if (instance.getPropertyUtils().isReadable(orig, name) &&
                    instance.getPropertyUtils().isWriteable(dest, name)) {
                try {
                    Object value =
                            instance.getPropertyUtils().getSimpleProperty(orig, name);

                    //比较值
                    Object valueOld =
                            instance.getPropertyUtils().getSimpleProperty(compareObj, name);
                    if(valueOld!=null&&!valueOld.equals(value)) {
                        instance.copyProperty(dest, name, value);
                    }else if(valueOld==null&&value!=null){
                        instance.copyProperty(dest, name, value);
                    }
                } catch (NoSuchMethodException e) {
                    // Should not happen
                }
            }
        }
    }
    /**
     * 金额分转元 并保留几位小数 的Double类型数据
     */
    public static Double getDoubleFormatLongMoney(Long balance, int bl) {
        if(balance == null){
            return null;
        }
        if(balance.longValue() == 0 ){
            return 0.0;
        }
        Double money = ((double)balance)/100;
        BigDecimal bg = new BigDecimal(money);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return re;
    }


    /**
     * 保留多少位小数
     */
    public static Double getDoubleFormat(Double number, int bl) {
        if(number == null){
            return null;
        }
        BigDecimal bg = new BigDecimal(number);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return re;
    }

    /**
     * 保留多少位小数
     */
    public static Double getDoubleFormatDown(Double number, int bl) {
        if(number == null){
            return null;
        }
        BigDecimal bg = new BigDecimal(number);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_DOWN).doubleValue();
        return re;
    }


    /**
     * 算出两个时间相差多少个小时
     */
    public static Long getTimeDifference(Date startTime,Date endTime){
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // 获得两个时间的毫秒时间差异
        long diff = endTime.getTime() - startTime.getTime();
        // 计算差多少天
        long day = diff / nd * 24;
        // 计算差多少小时
        long hour = diff % nd / nh;
        return day + hour;
    }

    public static BigDecimal objToLongDiv100(Long number){
        if(number != null && number > 0){
            BigDecimal bigA = new BigDecimal(number);
            BigDecimal bigB = new BigDecimal(100);
            return bigA.divide(bigB);
        }else{
            return new BigDecimal(0);
        }
    }

    /**
     * 数据 * 100
     *
     * @param str
     * @return
     */
    public static long objToLongMul100(String str) {
        if (objToFloat(str) != null) {
            BigDecimal bigA = new BigDecimal(str);
            BigDecimal bigB = new BigDecimal(100);
            return bigA.multiply(bigB).longValue();
        } else {
            return 0;
        }
    }


    public static Float objToFloat(String str) {
        if (StringUtils.isNotBlank(str)) {
            return Float.parseFloat(str);
        } else {
            return null;
        }
    }

    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static Long mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).longValue();
    }

    public static Long multiply(String value){
        if(StringUtils.isBlank(value) || !isNumber(value)){
            return null;
        }
        BigDecimal bd = new BigDecimal(100);
        return new BigDecimal(value).multiply(bd).longValue();
    }
    public static String divide(long value){
        if(-1 == value){
            return "";
        }
        BigDecimal bd = new BigDecimal(100);
        return (new BigDecimal(value).divide(bd).toString());
    }


    /*
     * 获取大图片
     */
    public static String getBigPicUrl(String url) {
        if (StringUtils.isEmpty(url)) {
            return "";
        }
        String bigUrl;
        url = url.split("\\?")[0];
        int idx = url.lastIndexOf(".");
        bigUrl = url.substring(0, idx) + "_big" + url.substring(idx);
        return bigUrl;
    }

    private static boolean isNumber(String str){
        String reg = "^[0-9]+(.[0-9]+)?$";
        return str.matches(reg);
    }

}

