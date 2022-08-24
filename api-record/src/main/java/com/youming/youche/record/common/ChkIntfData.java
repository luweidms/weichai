package com.youming.youche.record.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公用检查接口数据类
 *
 * @author wangbq
 */
public class ChkIntfData {

    /**
     * 检查车牌是否正确
     *
     * @param plateNumber
     * @return
     * @throws Exception
     */
    public static boolean chkPlateNumber(String plateNumber)  {
        String chk1 = "^[\u4e00-\u9fa5]{1}[A-Z_0-9]{6}$";
        String chk14 = "^[\u4e00-\u9fa5]{1}[A-Z_0-9]{5}[\u4e00-\u9fa5]{1}$";
        String chk2 = "^[A-Z_0-9]{5}[\u4e00-\u9fa5]{1}$";
        String chk3 = "^[A-Z]{2}[A-Z_0-9]{7}$";
        String chk4 = "^[A-Z]{2}[-]{1}[0-9]{5}$";
        String chk5 = "^[A-Z]{2}[0-9]{5}$";
        String chk6 = "^[A-Z]{2}[A-Z_0-9]{5}$";
        String chk7 = "^[A-Z]{2}[\u4e00-\u9fa5]{1}[A-Z_0-9]{4}$";
        String chk8 = "^[A-Z]{2}[0-9]{8}$";
        String chk9 = "^[A-Z]{2}[\u4e00-\u9fa5]{1}[A-Z_0-9]{5}$";
        String chk10 = "^[A-Z]{2}[\u4e00-\u9fa5]{1}[0-9]{5}$";
        String chk11 = "^[A-Z]{2}[0-9]{5}$";
        String chk12 = "^[A-Z]{2}[0-9]{7}$";
        String chk13 = "^[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{3}$";
        String chk15 = "^[\u4e00-\u9fa5]{1}[A-Z_0-9]{7}$";
        String chk16 = "^[\u4e00-\u9fa5]{1}[A-Z_0-9]{5}港$";
        String[] chk = new String[]{chk1, chk14, chk2, chk3, chk4, chk5, chk6, chk7, chk8, chk9, chk10, chk11, chk12, chk13, chk15, chk16};
        for (int i = 0; i < chk.length; i++) {
            Pattern pat = Pattern.compile(chk[i]);
            Matcher mat = pat.matcher(plateNumber);
            if (mat.matches()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查是否为小数
     * @param str
     * @return
     */
    public static boolean isPositiveDecimal(String str){
        Pattern pattern = Pattern.compile("\\d+\\.\\d+$|-\\d+\\.\\d+$");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }

}
