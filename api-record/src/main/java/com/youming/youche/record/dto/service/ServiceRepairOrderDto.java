package com.youming.youche.record.dto.service;

import lombok.Data;

import java.io.Serializable;

/**
 * @author hzx
 * @description 车辆维保查询输出
 * @date 2022/1/20 11:26
 */
@Data
public class ServiceRepairOrderDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    private String id;
    /**
     * 用户ID
     */
    private Long eid;
    /**
     * 工单号(内)
     */
    private String orderCode;
    /**
     * 业务单号(外)
     */
    private String orderSn;
    /**
     * 创建时间
     */
    private String createDate;
    /**
     * 计划开始时间
     */
    private String scrStartTime;
    /**
     * 计划结束时间
     */
    private String scrEndTime;
    /**
     * 工单类型，’GHCWX’-维修，’GHCBY’-保养
     */
    private String workType;
    private String workTypeName;
    /**
     * 维修站点名称
     */
    private String shopName;
    private String itemCount;
    /**
     * 消费总金额(分)
     */
    private Long totalAmount;
    /**
     * 车牌号
     */
    private String carNo;
    /**
     * 服务商名称
     */
    private String serviceName;
    /**
     * 司机姓名
     */
    private String contractName;
    /**
     * 手机号码
     */
    private String contractMobile;
    /**
     * 进厂时间
     */
    private String inFactoryTime;
    /**
     * 维保结束时间
     */
    private String outFactoryTime;
    /**
     * 订单状态 1申请待审核 2申请不通过 3待接单 4开单待确认 5开单待审核 6开单不通过 7维保中  8完成待确认 9完成待审核 10完成不通过 11待支付 12已完成
     */
    private Integer orderStatus;
    private String orderStatusName;
    /**
     * 维保开始时间
     */
    private String repairStartTime;
    /**
     * 支付时间
     */
    private String repairEndTime;
    /**
     * 车辆品牌型号
     */
    private String brandModel;
    /**
     * 上单里程数
     */
    private String lastOrderMileage;
    /**
     * 进厂公里数
     */
    private String carMileage;
    /**
     * 品牌单价
     */
    private String brandPrice;
    /**
     * 租户名称(车队全称)
     */
    private String tenantName;
    /**
     * 是否系统自动打款,0手动核销，1系统自动打款
     */
    private String isAutomatic;
    /**
     * 核算金额
     */
    private String checkAmount;

    /**
     * 订单状态
     * 订单状态 1申请待审核 2申请不通过 3待接单 4工单待确认 5工单待审核 6工单不通过 7维保中  8完成待确认   11待支付 12已完成 13废弃
     * 14申请审核中 15工单审核中 16工单支付中。
     */
    private Integer updValue;

    /**
     * 审核
     */
    private Integer isAuth;

    /**
     * 申请人
     */
    private String opName;

}
