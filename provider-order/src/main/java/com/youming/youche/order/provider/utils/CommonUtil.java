package com.youming.youche.order.provider.utils;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.commons.util.RedisUtil;
import com.youming.youche.conts.EnumConsts;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.TimeUtil;
import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {

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
     * 单位毫升转升 并保留几位小数 的Double类型数据
     */
    public static Double getDoubleFormatLong(Long balance, int bl) {
        if(balance == null){
            return null;
        }
        if(balance.longValue() == 0 ){
            return 0.0;
        }
        Double money = ((double)balance)/10000;
        BigDecimal bg = new BigDecimal(money);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return re;
    }

    public static boolean isCheckMobiPhoneNew(String billId) {
        String reg="^((0\\d{2,3}-\\d{7,8})|(1[357849]\\d{9}))|(0\\d{2,3}\\d{7,8})$";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(billId);
        return mat.matches();
    }

    /**
     * 获取某个月开始天和结束天
     * 1:第一天 2：最后一天
     */
    public static Date getOneDayTime(String date, int type)  {
        String[] dates = date.split("-");
        int year = Integer.valueOf(dates[0]), month = Integer.valueOf(dates[1]);

        YearMonth yearMonth = YearMonth.of(year, month);
        ZonedDateTime zonedDateTime = null;
        if(type == 1){
            LocalDate localDate = yearMonth.atDay(1);
            LocalDateTime startOfDay = localDate.atStartOfDay();
            zonedDateTime = startOfDay.atZone(ZoneId.of("Asia/Shanghai"));
        }else {
            LocalDate endOfMonth = yearMonth.atEndOfMonth();
            LocalDateTime localDateTime = endOfMonth.atTime(23, 59, 59, 999);
            zonedDateTime = localDateTime.atZone(ZoneId.of("Asia/Shanghai"));
        }
        return Date.from(zonedDateTime.toInstant());
    }
    /**
     * todo 友车caoyajie订单生成ID
     * @return
     */
    static final long ID = 10017209L;
    public static long zhCreateOrderId(LoginInfo user,Integer state){
        long time = System.currentTimeMillis();
        Long vipId =user.getId() % 10000;
        return time+vipId+ID+state;
 //       return ID + LocalDateTime.now().getNano()+ getAtomicCounter();
    }
    private static AtomicInteger counter = new AtomicInteger(0);

    public static long getNotNullValue(Long value){
        return value!=null?value.longValue():0l;
    }
    /**生成加油记录司机单号*/
    static final long CONSUME_OIL_ORDER_ID_TO_DRIVER = 0L;
    public static String createDriverConsumeOilOrderId(){
        return (CONSUME_OIL_ORDER_ID_TO_DRIVER +getAtomicCounter()+"");
    }
    /**生成加油记录司机单号*/
    static final long CONSUME_OIL_ORDER_ID_TO_DRIVERs = 0L;
    public static String createDriverConsumeOilOrderIds(Long incr){
        return (CONSUME_OIL_ORDER_ID_TO_DRIVER + incr+"");
    }
    public static String multiply(String value,String defaultValue){
        if("".equals(value)||"-1".equals(value)){
            return String.valueOf(-1.0D);
        }
        if(isNumber(value)){
            if(isNumber(defaultValue)){
                BigDecimal bd = new BigDecimal(defaultValue);
                return String.valueOf(new BigDecimal(value).multiply(bd).longValue());
            }
            return String.valueOf(value);
        }
        return String.valueOf(-1.0D);
    }

    public static String divide(long value){
        if(-1 == value){
            return "";
        }
        BigDecimal bd = new BigDecimal(100);
        return (new BigDecimal(value).divide(bd).toString());
    }

    public static Double divide(long value,long defaultValue){
        if(-1 == value){
            return -1.0d;
        }
        BigDecimal bd = new BigDecimal(defaultValue);
        return (new BigDecimal(value).divide(bd).doubleValue());
    }
    /**

     * 长生消息id

     */

    public static long getAtomicCounter() {
        if (counter.get() > 999999) {
            counter.set(1);

        }

        long time = System.currentTimeMillis();

        long returnValue = time * 100 + counter.incrementAndGet();

        return returnValue;

    }

    private static long incrementAndGet() {
        return counter.incrementAndGet();

    }

    /**
     * 两个时间相差多少天
     * @param date1
     * @param date2
     * eg:2017-06-04 00:00:00    2017-06-06 09:00:00 返回3天
     * eg:2017-06-04 00:00:00    2017-06-04 09:00:00 返回1天
     * @throws ParseException
     */
    public static int getDifferDay(LocalDateTime date1, LocalDateTime date2)  {
        Duration duration = Duration.between(date1,date2);
        return Integer.parseInt(duration.toDays()+"");
    }
    /**
     * 生成流水号 5+yyMMddHHmmss+3位随机数
     *
     * @return
     */
    public static long createSoNbr() {
        long code = 0;
            Random r = new Random();
            int randomNumber = r.nextInt(899) + 100;
            String userIdStr = "5"
                    + DateUtil.formatDateByFormat(TimeUtil.getDataTime(),
                    "yyMMddHHmmss") + randomNumber;
            code = Long.parseLong(userIdStr);
        return code;
    }

    /** * 判断字符串是否是数字 */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value) || isLong(value);
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

    /** * 判断字符串是否是数字不为空的 */
    public static boolean isNumberNotNull(String value) {
        if(StringUtils.isNotEmpty(value)){
            return isInteger(value) || isDouble(value) || isLong(value);
        }
        return true;
    }

    public static Long multiply(String value){
        if(StringUtils.isBlank(value) || !isNumber(value)){
            return null;
        }
        BigDecimal bd = new BigDecimal(100);
        return new BigDecimal(value).multiply(bd).longValue();
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

}
