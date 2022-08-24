package com.youming.youche.record.common;


import com.youming.youche.commons.util.DataFormat;
import com.youming.youche.util.DateUtil;
import com.youming.youche.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @version:
 * @Title: CommonUtils
 * @Package: com.youming.youche.common
 * @Description: TODO(用一句话描述该文件做什么)
 * @author:DengYuanYe
 * @date: 2021/12/25 15:57
 * @company:
 */
@RequiredArgsConstructor
public class CommonUtil {


    public static Number transStringToNumber(String str) {
        if (StringUtils.isBlank(str)) {
            return null;
        }
        if (isNumber(str)) {
            Number num = NumberUtils.toDouble(str);
            return num;
        }
        return null;
    }

    /**
     * 判断字符串是否是数字
     */
    public static boolean isNumber(String value) {
        return isInteger(value) || isDouble(value) || isLong(value);
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
     * 生成流水号 5+yyMMddHHmmss+3位随机数
     *
     * @return
     */
    public static long createSoNbr()  {
        long code = 0;
        try {
            Random r = new Random();
            int randomNumber = r.nextInt(899) + 100;
            String userIdStr = "5"
                    + DateUtil.formatDateByFormat(TimeUtil.getDataTime(),
                    "yyMMddHHmmss") + randomNumber;
            code = Long.parseLong(userIdStr);
        } catch (Exception e) {
        }
        return code;
    }
    public static void transYuanFenToFen(List<String> keyList, Map map) {
        BigDecimal dv = new BigDecimal(100);
        try {
            for (String string : keyList) {
                String yuanValue = DataFormat.getStringKey(map, string);
                if (isNumber(yuanValue)) {
                    long vel = new BigDecimal(yuanValue).multiply(dv).longValue();
                    map.put(string, vel);
                }
            }
        } catch (Exception e) {
        }
    }

    public static String divide(long value) {
        if (-1 == value) {
            return "";
        }
        BigDecimal bd = new BigDecimal(100);
        return (new BigDecimal(value).divide(bd).toString());
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
            if (value.contains("."))
                return true;
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 判断字符串是否是数字不为空的
     */
    public static boolean isNumberNotNull(String value) {
        if (StringUtils.isNotEmpty(value)) {
            return isInteger(value) || isDouble(value) || isLong(value);
        }
        return true;
    }

    /**
     * 讲多个字符串转成List
     * @param string 以逗号分隔的字符串
     */
    public static List<String> convertStringList(String string) {
        if (org.apache.commons.lang.StringUtils.isBlank(string)) {
            return null;
        }
        String[] strs = string.split(",");
        return Arrays.asList(strs);
    }
    /**
     * 将多个id转换成List
     *
     * @param ids 以逗号分隔的数字字符串
     */
    public static List<Long> convertLongIdList(String ids) {
        List<Long> idList = new ArrayList<>();
        if (StringUtils.isNotEmpty(ids)) {
            String[] entityIdStrings = ids.split(",");
            for (String entityId : entityIdStrings) {
                idList.add(Long.valueOf(entityId));
            }
        }
        return idList;
    }

    /**
     * 保留多少位小数
     */
    public static Double getDoubleFormat(Double number, int bl) {
        if (number == null) {
            return null;
        }
        BigDecimal bg = new BigDecimal(number);
        double re = bg.setScale(bl, BigDecimal.ROUND_HALF_UP).doubleValue();
        return re;
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


    public static List strToList(String vstr, String classType) {
        if (StringUtils.isBlank(vstr)) {
            return new ArrayList<>();
        }
        String[] arr = vstr.split(",");
        List rtnList = new ArrayList<>();


        if ("long".equals(classType)) {
            for (int i = 0; i < arr.length; i++) {
                if (StringUtils.isNumeric(arr[i])) {
                    rtnList.add(Long.parseLong(arr[i]));
                }
            }
        } else if ("double".equals(classType)) {
            for (int i = 0; i < arr.length; i++) {

                if (StringUtils.isNumeric(arr[i])) {
                    rtnList.add(Double.parseDouble(arr[i]));
                }
            }
        } else if ("int".equals(classType)) {
            for (int i = 0; i < arr.length; i++) {

                if (StringUtils.isNumeric(arr[i])) {
                    rtnList.add(Integer.parseInt(arr[i]));
                }
            }
        } else {
            for (int i = 0; i < arr.length; i++) {

                rtnList.add(arr[i]);
            }
        }

        return rtnList;
    }
    public static void transFeeFenToYuan(List<String> keyList,Map map){
        BigDecimal dv = new BigDecimal(100);
        try {
            for (String string : keyList) {
                String fenValue = DataFormat.getStringKey(map,string);
                if(StringUtils.isNotBlank(fenValue)){
                    map.put(string,new BigDecimal(fenValue).divide(dv).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
                }
            }
        } catch (Exception e) {
        }
    }

    public static boolean isCheckMobiPhone(String billId){
        Pattern pat = Pattern.compile("^((0\\d{2,3}-\\d{7,8})|(1[357849]\\d{9}))$");
        Matcher mat = pat.matcher(billId);
        return mat.matches();
    }
    public static boolean isCheckMobiPhoneNew(String billId){
        String reg="^((0\\d{2,3}-\\d{7,8})|(1[357849]\\d{9}))|(0\\d{2,3}\\d{7,8})$";
        Pattern pat = Pattern.compile(reg);
        Matcher mat = pat.matcher(billId);
        return mat.matches();
    }


//    /**
//     * 检测正确的手机号码
//     *
//     * @param billId
//     * @return
//     * @throws Exception
//     */
//    public static boolean isCheckPhone(String billId) throws Exception {
////		Pattern pat = Pattern.compile("^(13[0-9]|15[0-9]|18[0-9]|14[57]|17[0-9]|19[0-9])[0-9]{8}$");
////		Matcher mat = pat.matcher(billId);
////		return mat.matches();
//        if(billId !=null && !billId.equals("")){
//            SysCfg sysCfg =  (SysCfg) redisUtil.get(EnumConsts.SysCfg.SYS_CFG_NAME.concat(EnumConsts.SysCfg.CHK_PHONE));
//            if(sysCfg!=null && !sysCfg.getCfgValue().equals("")){
//                String codeValue = sysCfg.getCfgValue();
//                Pattern pat = Pattern.compile(codeValue);
//                Matcher mat = pat.matcher(billId);
//                if(mat.matches()){
//                    return true;
//                }
//            }
//        }
//        return false;
//    }


    public static String getCmCustomerLineCodeRule(Long lineId) {

        if (lineId == null) {
            return "L00000001";
        }

        String lineCodeRule = String.valueOf(lineId + 1L);

        for (int i = lineCodeRule.trim().length(); i < 8; i++) {
            lineCodeRule = "0" + lineCodeRule;
        }
        return "L" + lineCodeRule;
    }

    /**
     * 解析地址
     * @param address
     * @return
     */
    public static Map<String,String> addressResolution(String address){
        String regex="(?<province>[^省]+自治区|.*?省|.*?行政区|.*?市|北京?|上海?|天津?|重庆?)(?<city>[^市]+自治州|.*?地区|.*?行政单位|.+盟|市辖区|.*?市|.*?县)(?<county>[^县]+县|.+区|.+市|.+旗|.+海域|.+岛)?(?<town>[^区]+区|.+镇)?(?<village>.*)";
        Matcher m=Pattern.compile(regex).matcher(address);
        String province=null,city=null,county=null,town=null,village=null;
        Map<String,String> row=new LinkedHashMap<String,String>();
        while(m.find()){
            province=m.group("province");
            row.put("province", province==null?"":province.trim());
            city=m.group("city");
            row.put("city", city==null?"":city.trim());
            county=m.group("county");
            row.put("county", county==null?"":county.trim());
            town=m.group("town");
            row.put("town", town==null?"":town.trim());
            village=m.group("village");
            row.put("village", village==null?"":village.trim());
        }
        return row;
    }

    /**
     * 检验是否double
     * @param str
     * @return
     */
    public static boolean checkIsDouble(String str) {
        if (null == str || "".equals(str)) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }

    public static Double getDoubleByObject(Object obj){
        return obj != null ? Double.valueOf(String.valueOf(obj)) : -1;
    }

    /***
     * 车辆类型转会员类型
     * @param vehicleClass
     * @return
     */
    public static int vehicleType2CarType(int vehicleClass) {
        if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS1) {
            return SysStaticDataEnum.CAR_USER_TYPE.OWN_CAR;
        } else if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS5) {
            return SysStaticDataEnum.CAR_USER_TYPE.SOCIAL_CAR;
        } else if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS2) {
            return SysStaticDataEnum.CAR_USER_TYPE.BUSINESS_CAR;
        } else if (vehicleClass == SysStaticDataEnum.VEHICLE_CLASS.VEHICLE_CLASS4) {
            return SysStaticDataEnum.CAR_USER_TYPE.ATTACH_CAR;
        }
        return -1;
    }

}
