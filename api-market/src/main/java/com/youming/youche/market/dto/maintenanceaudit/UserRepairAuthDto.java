package com.youming.youche.market.dto.maintenanceaudit;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRepairAuthDto implements Serializable {
    private String repairCode;
    private String serviceName;
    private String productName;
    private String plateNumber;
    private String driverName;
    private String driverBill;
    private String repairDateBegin;
    private String repairDateEnd;
    private Integer state;
    private Boolean todo;
}
