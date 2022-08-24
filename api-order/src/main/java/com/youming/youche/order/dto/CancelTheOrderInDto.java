package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class CancelTheOrderInDto implements Serializable {


    private static final long serialVersionUID = 7574719037408386335L;


    private Long userId;//用户编号
    private String vehicleAffiliation; //资金渠道
    private Long amountFee;//可用金额单位(分)
    private Long virtualOilFee;//虚拟油卡金额单位(分)
    private Long entityOilFee; //实体油卡金额单位(分)
    private Long etcFee;//ETCFee金额单位(分)
    private Long orderId; //订单编号
    private Long tenantId; //订单开单租户id
    private Integer isNeedBill;//是否开票(0不开票，1承运方开票，2平台票)
    private Long arriveFee;//到付款
    private Integer isPayArriveFee;//是否已经支付了到付款(0未支付，1已支付)
}
