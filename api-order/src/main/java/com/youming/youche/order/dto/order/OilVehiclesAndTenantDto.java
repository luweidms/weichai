package com.youming.youche.order.dto.order;

import lombok.Data;

import java.io.Serializable;

@Data
public class OilVehiclesAndTenantDto implements Serializable {

    private static final long serialVersionUID = 726354098468156213L;
    private String plateNumber;
    private String tenantName;

}
