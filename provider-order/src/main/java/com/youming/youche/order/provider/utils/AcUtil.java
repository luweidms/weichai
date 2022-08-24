package com.youming.youche.order.provider.utils;

import com.youming.youche.conts.EnumConsts;

import static com.youming.youche.order.provider.utils.CommonUtil.CONSUME_OIL_ORDER_ID_TO_DRIVER;

public class AcUtil {

    /**
     * 创建订单号：SS + 八位数字(从0开始)
     */
    public static String createOilRechargePaymentnum(String soIndex) {
        String tempOrderId = CommonUtil.createDriverConsumeOilOrderId();
        Long orderId = Long.valueOf(tempOrderId);
        String order = "" ;
        if (orderId < 10000000L) {
            if (tempOrderId.length() == 1) {
                order = soIndex+"0000000" + tempOrderId;
            } else if (tempOrderId.length() == 2) {
                order = soIndex+"000000" + tempOrderId;
            } else if (tempOrderId.length() == 3) {
                order = soIndex+"00000" + tempOrderId;
            } else if (tempOrderId.length() == 4) {
                order = soIndex+"0000" + tempOrderId;
            } else if (tempOrderId.length() == 5) {
                order = soIndex+"000" + tempOrderId;
            } else if (tempOrderId.length() == 6) {
                order = soIndex+"00" + tempOrderId;
            } else if (tempOrderId.length() == 7) {
                order = soIndex+"0" + tempOrderId;
            }
        } else {
            order = order + tempOrderId;
        }
        return order;
    }

    /**
     * 创建订单号：SS + 八位数字(从0开始)
     */
    public static String createDriverConsumeOilOrderId(Long incr) {
        String tempOrderId = CommonUtil.createDriverConsumeOilOrderIds(incr);
        Long orderId = Long.valueOf(tempOrderId);
        String order = "" ;
        if (orderId < 10000000L) {
            if (tempOrderId.length() == 1) {
                order = "SS0000000" + tempOrderId;
            } else if (tempOrderId.length() == 2) {
                order = "SS000000" + tempOrderId;
            } else if (tempOrderId.length() == 3) {
                order = "SS00000" + tempOrderId;
            } else if (tempOrderId.length() == 4) {
                order = "SS0000" + tempOrderId;
            } else if (tempOrderId.length() == 5) {
                order = "SS000" + tempOrderId;
            } else if (tempOrderId.length() == 6) {
                order = "SS00" + tempOrderId;
            } else if (tempOrderId.length() == 7) {
                order = "SS0" + tempOrderId;
            }
        } else {
            order = order + tempOrderId;
        }
        return order;
    }
}
