package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Date:2021/12/21
 */
@Data
public class VehicleDataInfoDto implements Serializable {

    /**
     * 车牌号码
     */
    private String plateNumber;

    /**
     * 司机
     */
    private String linkman;

    /**
     * 司机手机
     */
    private String mobilePhone;

    /**
     * 车队手机
     */
    private String billReceiverMobile;

    /**
     * 车主联系人
     */
    private String linkManName;

    /**
     * 车主手机
     */
    private String linkPhone;

    /**
     * 车型(车长)
     */
    private String vehicleLength;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 车型(类型)
     */
    private Integer vehicleStatus;

    /**
     * 注册时间开始
     */
    private Date startTime;

    /**
     * 注册时间结束
     */
    private Date endTime;

    /**
     * 认证状态
     */
    private Integer authStateIn;

    /**
     * 是否共享
     */
    private Integer shareFlgIn;

    private Integer isAuth;

    private Integer vehicleClass;

    /**
     * 车辆类型
     */
    private Integer vehicleGps;

    private Long tenantId;

    /**
     * 北斗购买车辆开始时间
     */
    private String bdEffectDate;

    /**
     * 北斗购买车辆结束时间
     */
    private String  bdInvalidDate;

    private Integer   pageNum;

    private Integer pageSize;

}
