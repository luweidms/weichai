package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleDataInfoiDto implements Serializable {
    private String plateNumber; // 车牌号码
    private String linkman; // 司机姓名
    private String mobilePhone; // 司机手机
    private String linkManName; // 归属车队
    private String linkPhone; // 车队手机
    private String vehicleLength; // 车辆类型
    private String tenantName; // 归属车队
    private Integer vehicleStatus; // 车联类型
    private Long provinceId; // 心愿线路开始省
    private Long cityId; // 心愿线路开始市
    private Long provinceIdDes; // 心愿线路结束省
    private Long cityIdDes; // 心愿线路结束市
    private String startTime; // 最后但完成开始时间
    private String endTime; // 最后单完成时间结束
    private Long currProvinceId; // 当前位置
    private Long currCityId; // 当前位置
    private Long tenantId; // 车队id
}
