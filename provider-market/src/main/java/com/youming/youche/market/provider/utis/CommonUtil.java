package com.youming.youche.market.provider.utis;

import com.youming.youche.commons.exception.BusinessException;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
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
    public static boolean isCheckMobiPhoneNew(String billId)  {
        String reg="^((0\\d{2,3}-\\d{7,8})|(1[357849]\\d{9}))|(0\\d{2,3}\\d{7,8})$";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(billId);
        return mat.matches();
    }
    public static Long multiply(String value){
        if(StringUtils.isBlank(value) || !isNumber(value)){
            return null;
        }
        BigDecimal bd = new BigDecimal(100);
        return new BigDecimal(value).multiply(bd).longValue();
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
     * double乘于100转换Long类型
     * @param str
     */
    public static Long getLongByString(String str){
        long type = 0L;
        if(StringUtils.isNotEmpty(str)){
            try{
                double dd = Double.valueOf(str)* 100;
                DecimalFormat df = new DecimalFormat("######0");
                type  = Long.valueOf(df.format(dd));
            }catch(Exception e){
                throw new BusinessException("你输入的金额有误！");
            }
        }
        return type;
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

    public static <V> void sort(List<V> list, Map<String,Boolean>... properties) {
        Collections.sort(list, new Comparator<V>() {
            public int compare(V o1, V o2) {
                if (o1 == null && o2 == null) return 0;
                if (o1 == null) return -1;
                if (o2 == null) return 1;
                int i = 0;
                for (Map<String, Boolean> property : properties) {
                    for(Map.Entry<String,Boolean> entry : property.entrySet()){
                        Comparator c = new BeanComparator(entry.getKey());
                        int result = c.compare(o1, o2);
                        boolean desc = entry.getValue();
                        if (result != 0) {
                            if(desc){
                                return -result;
                            }else {
                                return result;
                            }
                        }
                    }
                }
                return 0;
            }
        });
    }

    /**
     * Created by kiya
     * 数据分组
     * @param list
     * @param key
     * @param <K>
     * @param <V>
     * @return
     * @throws Exception
     */
    public static <K,V> Map<K, List<V>> groupDataByKey(List<V> list,String key) throws Exception{
        Map<K, List<V>> resultMap = new HashMap<>();
        try{
            for(V o : list){
                Class vo =o.getClass();
                String fieldNames ="get"+Character.toUpperCase(key.charAt(0))+key.substring(1, key.length());
                Method getMethod = vo.getMethod(fieldNames);
                //设置获取私有属性
                getMethod.setAccessible(true);
                Object object = getMethod.invoke(o);
                if(resultMap.containsKey(object)){
                    resultMap.get(object).add(o);
                }else{//map中不存在，新建key，用来存放数据
                    List<V> tmpList = new ArrayList<>();
                    tmpList.add(o);
                    resultMap.put((K) object, tmpList);
                }
            }
        }catch(Exception e){
            throw new Exception("按照数据进行分组时出现异常", e);
        }
        return resultMap;
    }
}
