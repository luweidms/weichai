package com.youming.youche.market.api.tenant;

import com.youming.youche.commons.base.IBaseService;
import com.youming.youche.market.domain.tenant.TenantVehicleRel;

/**
 * 车队与车辆关系表
 *
 * @author hzx
 * @date 2022/3/12 18:10
 */
public interface ITenantVehicleRelService extends IBaseService<TenantVehicleRel> {

    /**
     * 查询是否存在自由车vehiclecode
     *
     * @param vehicleCode  车牌号码
     * @param tenantId     车队id
     * @param vehicleClass 车辆类型
     */
    Long getZYCount(long vehicleCode, long tenantId, int vehicleClass);

}
