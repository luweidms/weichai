package com.youming.youche.market.commons;

import com.youming.youche.commons.exception.BusinessException;
import com.youming.youche.conts.SysStaticDataEnum;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderUtil {
    public static boolean checkDependTime(Date dependTime) {
        if(dependTime==null) {
            throw new BusinessException("靠台时间不能为空，请输入！");
        }
        if(dependTime.after(new Date())) {
            return true;
        }
        return false;
    }

    /**
     * 订单录入的时候，订单表的字段校验不为空的字段
     * @return
     */
    public static Map<String,String> getAddOrderInfoCheckNoBlank(){
        Map<String,String> returnMap=new HashMap<String, String>();
        returnMap.put("orderType", "订单类型");
        returnMap.put("sourceProvince", "起始地省份");
        returnMap.put("sourceRegion", "起始地城市");
//        returnMap.put("sourceCounty", "起始地县区");
        returnMap.put("desProvince", "目的地省份");
        returnMap.put("desRegion", "目的地城市");
//        returnMap.put("desCounty", "目的地县区");
        return  returnMap;
    }
    /**
     * 订单录入的时候，订单货品表的字段校验不为空的字段
     * @param type  固定线路，临时线路
     * @return
     */
    public static Map<String,String> getAddOrderGoodsCheckNoBlank(Integer type){
        Map<String,String> returnMap=new HashMap<String, String>();
        returnMap.put("source", "起始地的省市区");
        returnMap.put("des", "目的地省市区");
        //returnMap.put("goodsName", "货品名称");
        //returnMap.put("square", "货物体积");
        //returnMap.put("weight", "货物重量");
        //returnMap.put("customName", "客户简称");

        if(OrderConsts.OrderType.FIXED_LINE==type){
            //固定线路
            returnMap.put("customUserId", "客户id");
        }else if(OrderConsts.OrderType.TEMPORARY_LINE==type){
            //临时线路
//            returnMap.put("address", "公司地址");
        }
        /*returnMap.put("reciveName", "收货人");
        returnMap.put("recivePhone", "收货人电话");*/
        //returnMap.put("contactPhone", "托运人联系电话");
        returnMap.put("nand", "起始地经纬度");
        returnMap.put("eand", "起始地经纬度");
        returnMap.put("nandDes", "目的地经纬度");
        returnMap.put("eandDes", "目的地经纬度");
        //returnMap.put("vehicleLengh", "需求车长");
//			returnMap.put("vehicleStatus", "需求车型");
        returnMap.put("localUser", "跟单人员id");
        returnMap.put("localPhone", "跟单人员电话");
        returnMap.put("localUserName", "跟单人员名称");


        //returnMap.put("reciveType", "回单类型");
        //returnMap.put("contactName", "托运人");

        //returnMap.put("reciveAddr", "回单地址");
        returnMap.put("companyName", "客户名称");

        //returnMap.put("linkName", "联系人");
        //returnMap.put("linkPhone", "客户联系电话");

        return  returnMap;
    }
    /**
     * 订单录入的时候，订单调度表的字段校验不为空的字段
     * @return
     */
    public static Map<String,String> getAddOrderSchedulerCheckNoBlank(Integer vehicleClass,Integer appointWay){
        Map<String,String> returnMap=new HashMap<String, String>();
        returnMap.put("appointWay", "调度方式");
        //returnMap.put("distance", "运输距离");
        returnMap.put("dependTime", "靠台时间");

        if(OrderConsts.AppointWay.APPOINT_CAR==appointWay){
            returnMap.put("plateNumber", "车牌号码");
            returnMap.put("carDriverPhone", "司机手机");
            returnMap.put("carDriverId", "司机id");
            returnMap.put("carDriverMan", "司机名称");
            returnMap.put("vehicleClass", "车辆类型");
            returnMap.put("desCounty", "目的地县区");
        }else if(OrderConsts.AppointWay.APPOINT_LOCAL==appointWay){
            returnMap.put("dispatcherName", "调度员名称");
            returnMap.put("dispatcherId", "调度员id");
            returnMap.put("dispatcherBill", "调度员手机");
        }

        return  returnMap;
    }

    /**
     * 校验车辆长度
     * @param carLength 实际车辆长度
     * @param vehicleLength 需求长度
     * @return
     */
    public static boolean checkVehicleLength(String carLength, String vehicleLength){
        if(StringUtils.isNotBlank(carLength) && StringUtils.isNotBlank(vehicleLength)){
            return Float.parseFloat(carLength) >= Float.parseFloat(vehicleLength) ? true : false;
        }
        return false;
    }


    /**
     * 校验车辆类型
     * @param carStatus
     * @param vehicleStatus
     * @return
     */
    public static boolean checkVehicleStatus(Integer carStatus, Integer vehicleStatus){
        if (carStatus != null && vehicleStatus != null) {
            if(vehicleStatus == 0){
                return true;
            }else{
                //厢车/高栏
                if (vehicleStatus.intValue() != SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_VAN_OR_HIGH_HURDLES.getType()) {
                    return carStatus.intValue() != vehicleStatus.intValue() ? false : true;
                }else{
                    return (carStatus == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_HIGH_HURDLES.getType()
                            || carStatus == SysStaticDataEnum.VEHICLE_STATUS_ENUM.VEHICLE_VAN.getType());
                }
            }
        }
        return false;
    }


    /**
     * 乘法
     * @param a
     * @param b
     * @return
     */
    public static long mul(String a , String b){
        if (StringUtils.isNotBlank(a) && StringUtils.isNotBlank(b)) {
            BigDecimal bigA = new BigDecimal(a);
            BigDecimal bigB = new BigDecimal(b);
            return bigA.multiply(bigB).longValue();
        }else{
            return -1;
        }
    }


    /**
     *
     * @param str
     * @return
     */
    public static float objToFloatDiv100(String str){
        if (StringUtils.isNotBlank(str)) {
            return Float.parseFloat(str) / 100;
        }else{
            return 0f;
        }
    }

    public static Float objToFloat(String str){
        if (StringUtils.isNotBlank(str)) {
            return Float.parseFloat(str);
        }else{
            return null;
        }
    }

    public static float objToFloatMul100(String str){
        if (StringUtils.isNotBlank(str)) {
            BigDecimal bigA = new BigDecimal(str);
            BigDecimal bigB = new BigDecimal(100);
            return bigA.multiply(bigB).floatValue();
        }else{
            return 0f;
        }
    }


    /**
     * 数据 * 100
     * @param str
     * @return
     */
    public static long objToLongMul100(String str){
        if(objToFloat(str)!=null) {
            BigDecimal bigA = new BigDecimal(str);
            BigDecimal bigB = new BigDecimal(100);
            return bigA.multiply(bigB).longValue();
        }else{
            return 0;
        }
    }

    public static long objToLongDiv100(String str){
        if(objToFloat(str)!=null) {
            BigDecimal bigA = new BigDecimal(str);
            BigDecimal bigB = new BigDecimal(100);
            return bigA.divide(bigB).longValue();
        }else{
            return 0;
        }
    }

    public static long objToLong0(String str){
        if (StringUtils.isNotBlank(str)) {
            if (str.indexOf(".") >= 0) {
                if(StringUtils.isNotBlank(str.split("\\.")[0])){
                    return Long.parseLong(str.split("\\.")[0]);
                }
            }else{
                return Long.parseLong(str);
            }
        }
        return 0;
    }

    public static Long objToLongNull(String str){
        if (StringUtils.isNotBlank(str)) {
            return Long.parseLong(str);
        }else{
            return null;
        }
    }

    public static String objToStringEmpty(Object str){
        if (str!=null) {
            return str.toString();
        }
        return "";
    }

    public static Long objToLong0(Object str){
        if (str!=null) {
            return Long.parseLong(str.toString());
        }
        return 0L;
    }
    public static Integer objToInteger0(Object str){
        if (str!=null && StringUtils.isNotBlank(str+"")) {
            return Integer.parseInt(str.toString());
        }
        return 0;
    }
}
