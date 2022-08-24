package com.youming.youche.system.utils;

import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtils {

    private static Pattern PAT_PHONE = Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|14[57]|17[0-9]|19[0-9])[0-9]{8}$");
//    private static Pattern PAT_PHONE = Pattern.compile("^1(3|4|5|7|8)\\\\d{9}$");
    final static int[] PARITYBIT = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    final static int[] POWER_LIST = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    final static Map<Integer, String> zoneNum = new HashMap<Integer, String>();

    static {
        zoneNum.put(11, "北京");
        zoneNum.put(12, "天津");
        zoneNum.put(13, "河北");
        zoneNum.put(14, "山西");
        zoneNum.put(15, "内蒙古");
        zoneNum.put(21, "辽宁");
        zoneNum.put(22, "吉林");
        zoneNum.put(23, "黑龙江");
        zoneNum.put(31, "上海");
        zoneNum.put(32, "江苏");
        zoneNum.put(33, "浙江");
        zoneNum.put(34, "安徽");
        zoneNum.put(35, "福建");
        zoneNum.put(36, "江西");
        zoneNum.put(37, "山东");
        zoneNum.put(41, "河南");
        zoneNum.put(42, "湖北");
        zoneNum.put(43, "湖南");
        zoneNum.put(44, "广东");
        zoneNum.put(45, "广西");
        zoneNum.put(46, "海南");
        zoneNum.put(50, "重庆");
        zoneNum.put(51, "四川");
        zoneNum.put(52, "贵州");
        zoneNum.put(53, "云南");
        zoneNum.put(54, "西藏");
        zoneNum.put(61, "陕西");
        zoneNum.put(62, "甘肃");
        zoneNum.put(63, "青海");
        zoneNum.put(64, "宁夏");
        zoneNum.put(65, "新疆");
        zoneNum.put(71, "台湾");
        zoneNum.put(81, "香港");
        zoneNum.put(82, "澳门");
        zoneNum.put(91, "外国");
    }

    /**
     * 检测正确的手机号码
     *
     * @param billId
     * @return
     * @throws Exception
     */
    public static boolean isCheckPhone(String billId) {
        Matcher mat = PAT_PHONE.matcher(billId);
        boolean matches = false;
        try {
            matches = mat.matches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;

    }

    /**
     * 身份证验证(内地和香港身份证校验)
     *
     * @param certNo 号码内容
     * @return 是否有效 null和"" 都是false
     */
    public static boolean isIDCard(String certNo) {

        if (isChinaIDCard(certNo) || isHKIdCard(certNo)) {
            return true;
        }
        return false;
    }

    /**
     * 内地身份证验证
     *
     * @param certNo 号码内容
     * @return 是否有效 null和"" 都是false
     */
    public static boolean isChinaIDCard(String certNo) {

        if (certNo == null || (certNo.length() != 15 && certNo.length() != 18))
            return false;
        final char[] cs = certNo.toUpperCase().toCharArray();
        //校验位数
        int power = 0;
        for (int i = 0; i < cs.length; i++) {
            if (i == cs.length - 1 && cs[i] == 'X')
                break;//最后一位可以 是X或x
            if (cs[i] < '0' || cs[i] > '9')
                return false;
            if (i < cs.length - 1) {
                power += (cs[i] - '0') * POWER_LIST[i];
            }
        }

        //校验区位码
        if (!zoneNum.containsKey(Integer.valueOf(certNo.substring(0, 2)))) {
            return false;
        }

        //校验年份
        String year = certNo.length() == 15 ? getIdcardCalendar() + certNo.substring(6, 8) : certNo.substring(6, 10);

        final int iyear = Integer.parseInt(year);
        if (iyear < 1900 || iyear > Calendar.getInstance().get(Calendar.YEAR))
            return false;//1900年的PASS，超过今年的PASS

        //校验月份
        String month = certNo.length() == 15 ? certNo.substring(8, 10) : certNo.substring(10, 12);
        final int imonth = Integer.parseInt(month);
        if (imonth < 1 || imonth > 12) {
            return false;
        }

        //校验天数
        String day = certNo.length() == 15 ? certNo.substring(10, 12) : certNo.substring(12, 14);
        final int iday = Integer.parseInt(day);
        if (iday < 1 || iday > 31)
            return false;

        //校验"校验码"
        if (certNo.length() == 15)
            return true;
        return cs[cs.length - 1] == PARITYBIT[power % 11];
    }

    /**
     * 获取年份
     *
     * @return
     */
    private static int getIdcardCalendar() {
        GregorianCalendar curDay = new GregorianCalendar();
        int curYear = curDay.get(Calendar.YEAR);
        int year2bit = Integer.parseInt(String.valueOf(curYear).substring(2));
        return year2bit;
    }

    public static boolean isHKIdCard(String idCard) {
        if (idCard == null || "".equals(idCard)) {
            return false;
        }
        return idCard.matches("[A-Z]{1}[0-9]{6}\\([0-9A]\\)");
    }

    public static boolean isCheckMobiPhoneNew(String billId) {
        String reg = "^((0\\d{2,3}-\\d{7,8})|(1[357849]\\d{9}))|(0\\d{2,3}\\d{7,8})$";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(billId);
        return mat.matches();
    }


    /**
     * 将多个id转换成List
     *
     * @param ids 以逗号分隔的数字字符串
     */
    public static List<Long> convertLongIdList(String ids) {
        List<Long> idList = new ArrayList<>();
        if (!StringUtils.isEmpty(ids)) {
            String[] entityIdStrings = ids.split(",");
            for (String entityId : entityIdStrings) {
                idList.add(Long.valueOf(entityId));
            }
        }
        return idList;
    }

    /**
     * 判断数组是否包含该数字
     * @param numbers 数组
     * @param value   判断数字
     *  true 包含 false不包含
     */
    public static boolean isContains(int [] numbers,int value){
        boolean flag = false;
        OUT:
        for(int i : numbers){
            if(i == value){
                flag = true;
                break OUT;
            }
        }
        return flag;
    }

    /**
     * 验证密码格式
     * @param value
     * @return
     */
    public static boolean isCheckPwd(String value){
        Pattern pat = Pattern.compile("^[\\dA-Za-z(!@#$%&)]{6,16}$");
        Matcher mat = pat.matcher(value);
        return mat.matches();
    }

}
