package com.youming.youche.record.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class IdleVehiclesVo implements Serializable {

    /**
     * 详细位置
     */
    private Long address;

    /**
     * 经度
     */
    private String lngStr;

    /**
     * 纬度
     */
    private String latStr;

    private String idleHour;
    /**
     * 车辆类型
     */
    private Long vehicleStatus;

    private String vehicleStatusName;

    /**
     * 车长
     */
    private Long vehicleLength;

    private String vehicleLengthName;
    /**
     *车牌号
     */
    private String plateNumber;
    /**
     * 半径
     */
    private Double radius;

    /**
     * 车队id
     */
    private Long  tenantId;

    private Double startHour;

    private Double endHour;
}
