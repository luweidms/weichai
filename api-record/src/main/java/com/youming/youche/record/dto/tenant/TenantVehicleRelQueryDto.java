package com.youming.youche.record.dto.tenant;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zengwen
 * @date 2022/4/14 13:37
 */
@Data
public class TenantVehicleRelQueryDto implements Serializable {

    /**
     * 关联表ID
     */
    private Long relId;

    /**
     * 车辆code
     */
    private Long vehicleCode;

    /**
     * 账单接收人手机号
     */
    private String billReceiverMobile;

    /**
     * 账单接收人用户编号
     */
    private Long billReceiverUserId;

    /**
     * 账单接收人名称
     */
    private String billReceiverName;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 司机ID
     */
    private Long driverUserId;

    /**
     * 司机名字
     */
    private String linkman;

    /**
     * 司机手机
     */
    private String mobilePhone;

    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 车辆总数
     */
    private Integer vehicleNum;
}
