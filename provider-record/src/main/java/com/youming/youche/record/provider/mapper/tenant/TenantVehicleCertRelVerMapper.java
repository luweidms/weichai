package com.youming.youche.record.provider.mapper.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.tenant.TenantVehicleCertRelVer;
import org.apache.ibatis.annotations.Param;

/**
 * @Date:2021/12/28
 */
public interface TenantVehicleCertRelVerMapper extends BaseMapper<TenantVehicleCertRelVer> {

    Integer updTenantVehicleCertRelVer(@Param("destVerState") Integer destVerState,
                                   @Param("vehicleCode")Long vehicleCode,
                                   @Param("tenantId")Long tenantId);
}
