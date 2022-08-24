package com.youming.youche.finance.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleListByDriverDto implements Serializable {

    private Long vehicleCode;
    private Long authState;
    private Long tenantId;
    private String plateNumber;

}
