package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Date:2022/2/23
 */
@Data
public class VehicleDataVo implements Serializable {

     /**
      * 车辆id
      */
    private Long vehicleCode;

    /**
     * 车牌
     */
    private String plateNumber;

    /**
     * 车辆类型
     */
    private Integer licenceType;

    /**
     * 车辆类型
     */
    private String licenceTypeName;


    /**
     * 车长
     */
    private String vehicleLength;

    /**
     * 车长
     */
    private String vehicleLengthName;

    /**
     * 车辆类型
     */
    private Integer vehicleStatus;

    /**
     * 车辆类型
     */
    private String vehicleStatusName;

    /**
     * 注册时间
     */
    private String createDate;

    /**
     * 认证状态
     */
    private Integer authState;

    /**
     * 认证状态
     */
    private String authStateName;

    /**
     * 是否显示审核按钮 0否 1是
     */
    private Integer isAuth;

    /**
     * 定位服务:0-无；1-G7；2-app定位; 3-易流
     */
    private Integer locationServ;

    /**
     * 定位服务:0-无；1-G7；2-app定位; 3-易流
     */
    private String  locationServName;



    /**
     * 司机id
     */
    private Long driverUserId;

    /**
     * 司机名称
     */
    private String linkman;

    /**
     * 司机手机
     */
    private String mobilePhone;


    /**
     * 是否共享 0否 1是
     */
    private Integer shareFlg;

    /**
     * 是否共享 0否 1是
     */
    private String shareFlgName;


    /**
     * 车队id
     */
    private Long tenantId;

    /**
     * 车队名称
     */
    private String tenantName;

    /**
     * 车队手机
     */
    private String linkPhone;

    /**
     * 车队联系人
     */
    private String linkManName;

    /**
     * 司机数量
     */
    private Integer driverNum;


    private String vehicleInfo;
}
