package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class VehicleDataInfoiVo implements Serializable {

    /**
     * 挂车id
     */
    private Long managementId;

    /**
     * 车类型
     */
    private Integer vehicleClass;

    /**
     * 车类型名称
     */
    private String vehicleClassName;
    /**
     * 车辆编号
     */
    private Long vehicleCode;
    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 牌照类型(1:整车，2：拖头)
     */
    private Integer licenceType;

    /**
     * 车长
     */
    private String vehicleLength;

    /**
     * 车状态
     */
    private Long vehicleStatus;

    /**
     * 最后运作订单时间
     */
    private String lastOrderDate;
    /**
     * 订单号
     */
    private Long orderId;

    /**
     *
     */
    private String driverUserId;
    /**
     * 联系人
     */
    private String linkMan;

    /**
     * 手机号码
     */
    private String mobilePhone;

    /**
     * 车队id
     */
    private Long tenantId;

    /**
     *归属车队
     */
    private String tenantName;

    /**
     * 租户联系人电话
     */

    private String linkPhone;

    /**
     * 省份ID
     */
    private Integer provinceId;

    /**
     * 市编码ID
     */
    private Integer cityId;

    /**
     * 县区ID
     */
    private Integer countyId;

    /**
     * 是否为自有车
     */
    private Integer isSelfVehicle;

    /**
     * 许可证类型名称
     */
    private String licenceTypeName;

    /**
     * 车状态名称
     */
    private String vehicleStatusName;

    /**
     * 车长名称
     */
    private String vehicleLengthName;

    /**
     * 车信息
     */
    private  String vehicleInfo;

    /**
     * 省名
     */
    private String provinceName;

    /**
     * 市名称
     */
    private String cityName;

    /**
     * 县名
     */
    private String countyName;

    /**
     * 是否绑卡
     */
    private String isBindCard;
}
