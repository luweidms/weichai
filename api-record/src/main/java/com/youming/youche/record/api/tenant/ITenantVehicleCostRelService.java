package com.youming.youche.record.api.tenant;

import com.baomidou.mybatisplus.extension.service.IService;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRel;
import com.youming.youche.record.domain.tenant.TenantVehicleRelVer;

import java.util.List;

public interface ITenantVehicleCostRelService extends IService<TenantVehicleCostRel> {

    /**
     * 查询车队车成本信息
     */
    TenantVehicleCostRel selectByRelId(Long relId);

    /**
     * 查询车队车成本信息
     *
     * @param plateNumber 车牌号
     * @param tenantId    车队id
     */
    TenantVehicleCostRel getTenantVehicleCostRel(String plateNumber, Long tenantId);

    /**
     * 查询车队车成本信息
     *
     * @param id        车辆编号
     * @param tenantId  车队id
     * @param beginDate 创建时间
     * @param endDate   创建时间
     */
    List<TenantVehicleCostRel> lists(Long id, Long tenantId, String beginDate, String endDate);

}
