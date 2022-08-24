package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ApplyRecordVehicleInfoDto implements Serializable {

    private static final long serialVersionUID = 5185719350224957201L;

    private String vehicleStatus; // 货箱结构
    private String vehicleLength; // 车长
    private String plateNumber; // 车牌

    private String vehicleTypeString; // 车辆类型
    private Boolean checked; // 查看详情的时候该车是否已经被选中

}
