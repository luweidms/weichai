package com.youming.youche.capital.provider.utis;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {
   final static String regex="^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\\\d{8}$";

    /** * 判断字符串是否是数字 */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value) || isLong(value);
    }
    public static boolean isCheckMobiPhone(String billId) throws Exception {
        Pattern pat = Pattern.compile("^((0\\d{2,3}-\\d{7,8})|(1[357849]\\d{9}))$");
        Matcher mat = pat.matcher(billId);
        return mat.matches();
    }
    public static boolean isCheckMobiPhoneNew(String billId) throws Exception {
        String reg="^((0\\d{2,3}-\\d{7,8})|(1[357849]\\d{9}))|(0\\d{2,3}\\d{7,8})$";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(billId);
        return mat.matches();
    }


    /**
     * 判断Long不为空 切大于0
     * @param num
     * @return
     */
    public static boolean isNotBlankToLong(Long num){
        return !isBlankToLong(num);
    }
    public static String divide(long value){
        if(-1 == value){
            return "";
        }
        BigDecimal bd = new BigDecimal(100);
        return (new BigDecimal(value).divide(bd).toString());
    }
    /** * 判断字符串是否是浮点数 */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /** * 判断字符串是否是整数 */
    public static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /** * 判断字符串是否是整数 */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 金额分转元 并保留几位小数 的Double类型数据,c除于多少
     */
    public static String getDoubleFormatLongMoney(Long balance, Integer bl,Integer c) {
        if(balance == null){
            return null;
        }
        if(balance.longValue() == 0 ){
            return "0.0";
        }
        Double money = ((double)balance)/100/c;
        BigDecimal bg = new BigDecimal(money);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return String.valueOf(re);
    }
    /**
     * 金额分转元 并保留几位小数 的Double类型数据
     */
    public static Double getDoubleFormatLongMoney(Long balance, Integer bl) {
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
     * 金额分转元 并保留几位小数 的Double类型数据
     */
    public static Double getDoubleFormatLongMoney(BigDecimal balance, Integer bl) {
        if(balance == null){
            return null;
        }
        if(balance.longValue() == 0 ){
            return 0.0;
        }
        double value = balance.doubleValue();
        Double money = value/100;
        BigDecimal bg = new BigDecimal(money);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return re;
    }
    /**
     * 校验枚举
     *
     * @param value
     * @param EnumKey
     * @return
     */
     public static boolean checkEnumIsNull(Integer value, int EnumKey) {
        if (value == null || value == EnumKey) {
            return true;
        }
        return false;
    }
    /**
     * 校验枚举
     *
     * @param value
     * @param EnumKey
     * @return
     */
    public static boolean checkEnumIsNotNull(Integer value, int EnumKey) {
        if (value != null && value == EnumKey) {
            return true;
        }
        return false;
    }
    /**
     * 手机
     * @param billId
     * @return
     */
   public static Boolean isCheckPhone(String billId) {
       if (billId.length() != 11) {
           return false;
       } else {
           Pattern p = Pattern.compile(regex);
           Matcher m = p.matcher(billId);
           boolean isMatch = m.matches();
           if (isMatch) {
               return true;
           } else {
               return false;
           }
       }
   }
    /**
     * 判断Long是否为空 切小于等于0
     * @param num
     * @return
     */
    public static boolean isBlankToLong(Long num){
        boolean isNull = false;
        if(num == null || num <= 0){
            isNull = true;
        }
        return isNull;
    }
}
