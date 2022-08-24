package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilEntityDto implements Serializable {
    //油卡卡号
    private String oilCarNum;
    //服务商名称
    private String serviceName;
    //创建时间
    private String createTime;
    //充值金额
    private String costEntityOilDouble;
    //代金券金额
    private String voucherAmountDouble;
    //待核销金额
    private String noVerificateEntityFeeDouble;
    //油卡状态
    private String verificationStateName;
    //核销时间
    private String verificationDate;
    //订单号
    private String orderId;
    //车牌号码
    private String plateNumber;
    //司机姓名
    private String carDriverMan;
    //司机手机号
    private String carDriverPhone;
    //车辆种类
    private String carStatusName;
    //靠台时间
    private String dependTime;
    //客户名称,,,,,
    private String customName;
    //线路起始
    private String sourceRegionName;
    //线路到达
    private String desRegionName;
    //线路名称
    private String sourceName;
    //车长
    private String vehicleLenghName;
    //车型
    private String vehicleStatusName;
}
