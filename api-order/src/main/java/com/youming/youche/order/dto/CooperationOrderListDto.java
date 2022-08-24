package com.youming.youche.order.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: luona
 * @date: 2022/5/20
 * @description: T0DO
 * @version: 1.0
 */
@Data
public class CooperationOrderListDto implements Serializable {
    private Long orderId;//订单号
    private String tenantName;//车队名称
    private String dependTime;//靠台时间
    private Integer sourceRegion;//起始点
    private Integer desRegion;//到达点
    private Integer orderState;//订单状态
    private Long preTotalFee;//预付总计金额
    private Long finalFee;//尾款金额
    private String plateNumber;//车牌号
    private Integer reciveType;//回单类型
    private Long totalFee;//中标价
    private String nand;//起始纬度
    private String eand;//起始经度
    private String nandDes;//目的纬度
    private String eandDes;//目的进度
    private Integer reciveState;//回单状态
    private Long preOilFee;//预付实体油
    private Long preOilVirtualFee;//预付虚拟油
    private Integer vehicleClass;//车辆类型 例如 招商挂靠车
    private Long salary;//主驾补贴
    private Long copilotSalary;//副驾补贴
    private Long driverSwitchSubsidy;//切换司机补贴
    private Float arriveTime;//运行时效
    private Integer paymentWay;//自有车付款方式
    private Long tenantId;//车队id
    private String localPhone;//跟单人手机
    private String localUserName;//跟单人名称
    private String source;//始发地详情
    private String des;//目的地详情
    private Long distance;//行驶距离
    private Long carDriverId;//主驾司机id
    private Integer arrivePaymentState;//是否支付到付款
    private Long arrivePaymentFee;//收入到付款



}
