package com.youming.youche.record.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class VehicleDataInfoVxDto implements Serializable {
    /**
     * 司机id
     */
    private Long driverUserId;
    /**
     * 车辆编号
     */
    private Long vehicleCode;
    /**
     * 车辆类别 1自有公司车 2招商挂靠车 3临时外调车 4外来挂靠车 5外调合同车
     */
    private Integer vehicleClass;

    private String vehicleClassName;
    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 租户姓名
     */
    private String tenantName;

    /**
     * 租户联系人
     */
    private String linkMan;

    /**
     * 联系人手机
     */
    private String mobilePhone;
}
