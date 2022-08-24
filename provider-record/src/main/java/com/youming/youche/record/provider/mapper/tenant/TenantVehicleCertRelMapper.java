package com.youming.youche.record.provider.mapper.tenant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.tenant.TenantVehicleCertRel;
import com.youming.youche.record.dto.TenantVehicleRelInfoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Date:2021/12/27
 */
public interface TenantVehicleCertRelMapper extends BaseMapper<TenantVehicleCertRel> {

    TenantVehicleRelInfoDto getTenantVehicleRelInfo(@Param("vehicleCode")Long vehicleCode,
                                                    @Param("tenantId") Long tenantId,
                                                    @Param("relId") Long relId);

    List<TenantVehicleCertRel> getTenantVehicleRelList(@Param("vehicleCode")Long vehicleCode,
                                                       @Param("tenantId") Long tenantId);

    /**
     * 查询车辆证照
     * @param plateNumber 车牌号
     * @return
     */
    List<TenantVehicleCertRel> findPlateNumber(String plateNumber);

}
