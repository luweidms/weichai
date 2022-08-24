package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;
@Data
public class UpdateTheOwnCarOrderInDto  implements Serializable {
    private Long masterUserId;
    /**
     *资金渠道
     */
    private String vehicleAffiliation;
    private Long originalEntiyOilFee;
    private Long updateEntiyOilFee;
    private Long originalFictitiousOilFee;
    private Long updateFictitiousOilFee;
    private Long originalBridgeFee;
    private Long updateBridgeFee;
    private Long originalMasterSubsidy;
    private Long updateMasterSubsidy;
    private Long originalSlaveSubsidy;
    private Long updateSlaveSubsidy;
    private Long slaveUserId;
    /**
     * 订单编号
     */
    private Long orderId;
    private Long tenantId;
    private Integer isNeedBill;
    private int oilUserType;//油使用方式
    private Integer originalOilConsumer;//原油消费对象
    private Integer updateOilConsumer;//修改后消费对象
    private Integer originalOilAccountType;//原油选择账户类型  油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
    private Integer updateOilAccountType;//修改后油选择账户类型  油来源账户类型:1授信账户，2已开票账户（客户油、返利油、转移油），3充值账户
    private Integer originalOilBillType;//原油选择票据类型  油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
    private Integer updateOilBillType;//修改后油选择票据类型  油票类型：1获取油票（非抵扣），2获取运输专票（抵扣）
}
