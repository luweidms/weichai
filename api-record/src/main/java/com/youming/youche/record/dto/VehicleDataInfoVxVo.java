package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleDataInfoVxVo implements Serializable {
    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 联系人姓名
     */
    private String carOwner;
    /**
     * 司机手机
     */
    private String carPhone;

    /**
     * 联系人姓名
     */
    private String linkman;

    /**
     * 司机手机
     */
    private String mobilePhone;

    /**
     * 车长
     */
    private Long vehicleLength;

    private String vehicleLengthName;
    /**
     * 车型(类型)
     */
    private Integer vehicleStatus;

    private String vehicleStatusName;

    private String vehicleClassName;

    /**
     * 车队名称
     */
    private String tenantName;
    /**
     * 车辆类别
     */
    private Integer vehicleClass;


    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 用户名称
     */
    private String name;

    /**
     * 用户编号
     */
    private Long userId;



    /**
     * 司机id
     */
    private Long driverUserId;


    /**
     * 车辆主键
     */
    private Long vehicleCode;


    /**
     * 空载油耗(升/百公里)
     */
    private Long loadEmptyOilCost;


    /**
     * 载重油耗(升/百公里)
     */
    private Long loadFullOilCost;


    /**
     * 是否使用车辆油耗
     */
    private Integer isUseCarOilCost;


    /**
     * 认证状态：1-未认证 2-认证中 3-已认证 4-认证失败
     */
    private Integer authState;

    private Integer sysAuthState;

    /**
     * 车辆类型(整车/拖头)
     */
    private Integer licenceType;

    /**
     * 副驾驶
     */
    private String copilotLinkman;

    /**
     * 副驾驶车主
     */
    private String copilotCarOwner;

    /**
     * 副驾驶电话
     */
    private String copilotCarPhone;

    /**
     * 联系人手机
     */
    private String linkmanMobilePhone;

    /**
     * 副驾驶驾驶员 ID
     */
    private Long copilotDriverId;


    private Long tmpTenantId;

    /**
     * 车队联系电话
     */
    private String tenantLinkPhone;


    private Long vehicleFlag;
    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 账单接收人用户编号
     */
    private Long billReceiverUserId;

    /**
     * 账单接收人姓名
     */
    private String billReceiverName;
}
