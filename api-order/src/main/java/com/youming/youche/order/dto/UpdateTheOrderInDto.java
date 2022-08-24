package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class UpdateTheOrderInDto  implements Serializable {
    private Long userId;
    /**
     *资金渠道
     */
    private String vehicleAffiliation;
    private Long originalAmountFee;
    private Long updateAmountFee;
    private Long originalVirtualOilFee;
    private Long updatelongVirtualOilFee;
    private Long originalEntityOilFee;
    private Long updateEntityOilFee;
    private Long originalEtcFee;
    private Long updateEtcFee;
    private Long orderId;
    private Long tenantId;
    private Integer isNeedBill;
    private Integer oilUserType;//油使用方式 客户油、车队油
    private Long originalArriveFee;
    private Long updateArriveFee;
    private Integer isPayArriveFee;//是否已经支付了到付款(0未支付，1已支付)
    private Integer originalOilConsumer;//原油消费对象
    private Integer updateOilConsumer;//修改后消费对象
    private Integer originalOilAccountType;//原油选择账户类型  油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
    private Integer updateOilAccountType;//修改后油选择账户类型  油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
    private Integer originalOilBillType;//原油选择票据类型  油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
    private Integer updateOilBillType;//修改后油选择票据类型  油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
}
