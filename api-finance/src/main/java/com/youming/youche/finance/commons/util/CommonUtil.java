package com.youming.youche.finance.commons.util;

import com.youming.youche.commons.domain.LoginInfo;
import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.util.TimeUtil;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class CommonUtil {

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value) || isLong(value);
    }
    /**
     * todo 友车caoyajie订单生成ID
     * @return
     */
    static final long ID = 10017210L;
    public static long zhCreateOrderId(LoginInfo user,Integer state){
        long time = System.currentTimeMillis();
        Long vipId =user.getId() % 10000;
        return time+vipId+ID+state;
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是整数
     */
    public static boolean isLong(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是浮点数
     */
    public static boolean isDouble(String value) {
        try {
            Double.parseDouble(value);
            return value.contains(".");
        } catch (NumberFormatException e) {
            return false;
        }
    }


    /**
     * 核销编码生成
     *
     * @return
     */
    static final long EXPENSE_NUMBER = 10000000L;

    public static String createExpenseNumber(Long incr) {
        return "EA" + (EXPENSE_NUMBER + incr);
    }

    /**
     * 车辆费用申请单号生成
     *
     * @return
     */
    static final long APPLY_NO = 10000000L;

    public static String createApplyNo(Long incr) {
        return "VC" + (APPLY_NO + incr);
    }

    /**
     * 金额分转元 并保留几位小数 的Double类型数据
     */
    public static Double getDoubleFormatLongMoney(Long balance, int bl) {
        if (balance == null) {
            return null;
        }
        if (balance.longValue() == 0) {
            return 0.0;
        }
        Double money = ((double) balance) / 100;
        BigDecimal bg = new BigDecimal(money);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return re;
    }

    public static BigDecimal getDoubleFormatLongMoneyBigDecimal(Long balance, int bl) {
        if (balance == null) {
            return null;
        }
        if (balance.longValue() == 0) {
            return new BigDecimal(0.0);
        }
        Double money = ((double) balance) / 100;
        BigDecimal bg = new BigDecimal(money);
        return bg.setScale(bl, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal getDoubleFormatLongMoneyBigDecimal(BigDecimal balance, int bl) {
        if (balance == null) {
            return null;
        }
        if (balance.longValue() == 0) {
            return new BigDecimal(0.0);
        }
        Double money = ((double)balance.longValue() / 100);
        BigDecimal bg = new BigDecimal(money);
        return bg.setScale(bl, BigDecimal.ROUND_HALF_UP);
    }

    public static String getDoubleFormatLongMoney(Long balance) {
        if(balance == null){
            return null;
        }
        if(balance.longValue() == 0 ){
            return 0.00+"";
        }
        Double money = ((double)balance)/100;
        BigDecimal bg = new BigDecimal(money);
        String re = bg.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        return re;
    }

    /*
     * 获取大图片
     */
    public static String getBigPicUrl(String url) {
        if (StringUtils.isEmpty(url))
            return "";
        String bigUrl;
        url = url.split("\\?")[0];
        int idx = url.lastIndexOf(".");
        bigUrl = url.substring(0, idx) + "_big" + url.substring(idx);
        return bigUrl;
    }

    /**生成油卡号*/
    static final long OALOAN_ID = 10000000L;
    public static String createOaloanId(Long incr){
        return (OALOAN_ID+ incr)+"";

    }
    /**
     * 创建BL账单号
     */
    public static String createBillNo() {
        String rd = getFixLenthRandString(3);
        return "BL" + System.currentTimeMillis() + rd;
    }

    public static String getFixLenthRandString(int strLength) {
        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }

    /**
     * 生成流水号 5+yyMMddHHmmss+3位随机数
     *
     * @return
     */
    public static long createSoNbr() {
        long code = 0;
            Random  r = new Random();
            int randomNumber = r.nextInt(899) + 100;
            String userIdStr = "5"
                    + DateUtil.formatDateByFormat(TimeUtil.getDataTime(),
                    "yyMMddHHmmss") + randomNumber;
            code = Long.parseLong(userIdStr);

        return code;
    }

    /**
     * 将多个id转换成List
     * @param ids 以逗号分隔的数字字符串
     */
    public static List<Long> convertLongIdList(String ids) {
        List<Long> idList = new ArrayList<>();
        if (StringUtils.isNotEmpty(ids)) {
            String[] entityIdStrings =  ids.split(",");
            for (String entityId : entityIdStrings) {
                idList.add(Long.valueOf(entityId));
            }
        }
        return idList;
    }

    /**
     * double乘于100转换Long类型
     * @param str
     */
    public static Long getLongByString(String str){
        long type = 0L;
        if(StringUtils.isNotEmpty(str) && isNumber(str)){
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

    public static Double getDoubleByObject(Object obj){
        return obj != null ? Double.valueOf(String.valueOf(obj)) : -1;
    }

    public static String divide(long value) {
        if (-1 == value) {
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
     * 获取指定月份的最后一天
     * @param date
     * @return
     */
    public static String getLastMonthDate(Date date){
        SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DATE, 1);
        calendar.add(Calendar.DATE, -1);
        return sf.format(calendar.getTime());
    }

    /**
     * 生成订单ID 2+yyMMddHHmmss+3位随机数
     *
     * @return
     * @throws Exception
     */
    public static long createOrderId() {
        long userId = 0;
        try {
            Random  r = new Random();
            int randomNumber = r.nextInt(899) + 100;
            String userIdStr = "2"
                    + DateUtil.formatDateByFormat(TimeUtil.getDataTime(),
                    "yyMMddHHmmss") + randomNumber;
            userId = Long.parseLong(userIdStr);
        } catch (Exception e) {
            throw new BusinessException("生成用户编号错误:" + e.getMessage());
        }
        return userId;
    }
}
