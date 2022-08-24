package com.youming.youche.record.dto;
import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleDataInfoExcelDto implements Serializable {

    private String plateNumber; // 车牌号
    private String linkman; // 联系人名称
    private String mobilePhone; // 联系人手机号
    private String billReceiverMobile; // 账单接受人手机号
    private String linkManName; // 联系人名称
    private String linkPhone; // 联系人手机号
    private String vehicleLength; // 车长
    private String tenantName; // 车队名称
    private Integer vehicleStatus; // 车辆状态
    private String startTime; // 开始时间
    private String endTime; // 结束时间
    private Integer authStateIn;
    private Integer shareFlgIn;
    private Integer isAuth; // 审核是否
    private Integer vehicleClass; // 车辆类型
    private Integer vehicleGps;
    private String bdEffectDate;
    private String bdInvalidDate;
    private String fieldName;
}