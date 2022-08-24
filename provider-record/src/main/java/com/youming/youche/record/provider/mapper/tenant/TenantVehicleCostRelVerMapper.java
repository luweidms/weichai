package com.youming.youche.record.provider.mapper.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.tenant.TenantVehicleCostRelVer;
import com.youming.youche.record.dto.TenantVehicleRelInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * @Date:2021/12/28
 */
public interface TenantVehicleCostRelVerMapper extends BaseMapper<TenantVehicleCostRelVer> {

    Long maxId();

    Integer updTenantVehicleCostRelVer(@Param("destVerState") Integer destVerState,
                                       @Param("vehicleCode")Long vehicleCode,
                                       @Param("tenantId")Long tenantId);

    TenantVehicleRelInfoDto getTenantVehicleRelInfoVer(@Param("vehicleCode")Long vehicleCode,
                                                       @Param("tenantId")Long tenantId,
                                                       @Param("relId")Long relId,
                                                       @Param("verState")Integer verState);

}
