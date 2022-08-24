package com.youming.youche.order.dto;

import com.youming.youche.order.domain.tenant.TenantVehicleCertRel;
import com.youming.youche.order.domain.tenant.TenantVehicleRel;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRel;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import lombok.Data;

import java.io.Serializable;

@Data
public class VehiclesListDto implements Serializable {
    private TenantVehicleRel tenantVehicleRel;

    private VehicleDataInfo vehicleDataInfo;

    private TenantVehicleCostRel tenantVehicleCostRel;

    private TenantVehicleCertRel tenantVehicleCertRel;
}
