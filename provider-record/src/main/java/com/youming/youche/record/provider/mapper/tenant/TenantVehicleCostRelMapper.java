package com.youming.youche.record.provider.mapper.tenant;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 车队与车辆成本表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface TenantVehicleCostRelMapper extends BaseMapper<TenantVehicleCostRel> {

    List<TenantVehicleCostRel> getTenantVehicleCostRelList(@Param("vehicleCode")Long vehicleCode,
                                                           @Param("tenantId")Long tenantId);
}
