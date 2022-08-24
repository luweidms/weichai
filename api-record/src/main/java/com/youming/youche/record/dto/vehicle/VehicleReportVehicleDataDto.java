package com.youming.youche.record.dto.vehicle;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleReportVehicleDataDto implements Serializable {
    private static final long serialVersionUID = 6079765309796208368L;

    /**
     * 车辆编号
     */
    private Long vehicleCode;

    /**
     * 车牌号
     */
    private String plateNumber;

    /**
     * 车辆类型
     */
    private Integer vehicleClass;

    /**
     * 车队id
     */
    private Long tenantId;

}
