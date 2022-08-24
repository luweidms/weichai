package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderListAppDto implements Serializable {

    private static final long serialVersionUID = -8409973908360401122L;
    /**
     * 接单车队ID
     */
    private Long toTenantId;
    private Long orderId;
    private String tenantName;
    private String dependTime;
    private String sourceRegion;
    private String desRegion;
    private String orderState;
    private String preTotalFee;
    private String finalFee;
    /**
     * 车牌号
     */
    private String plateNumber;
    /**
     * 回单类型
     */
    private String reciveType;
    private String totalFee;
    private String nand;
    private String eand;
    private String isCollection;
    private String nandDes;
    private String eandDes;
    private String reciveState;
    private String preOilFee;
    private String preOilVirtualFee;
    private String vehicleClass;
    private String salary;
    private Integer driverSwitchSubsidy;
    private String copilotSalary;
    /**
     * 到达时限
     */
    private String arriveTime;
    private Integer paymentWay;
    private Long tenantId;
    private String localPhone;
    private String localUserName;
    private String source;
    private String des;
    private String distance;
    private String carDriverId;
    private String estFee;
    private Integer arrivePaymentState;
    private Integer arrivePaymentFee;
    private Integer isHis;

}
