package com.youming.youche.record.dto;

import com.youming.youche.record.domain.tenant.TenantVehicleRelVer;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
@Data
public class VehicleDto implements Serializable {
    private List<VehicleDataInfo> vehicleDataInfos; // 车辆基础信息
    private List<TenantVehicleRelVer> tenantVehicleRelVers; // 车队与车辆关系信息
}
