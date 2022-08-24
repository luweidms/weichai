package com.youming.youche.record.provider.mapper.tenant;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.tenant.TenantVehicleRelVer;
import com.youming.youche.record.dto.tenant.TenantVehicleRelQueryDto;
import com.youming.youche.record.vo.tenant.TenantVehicleRelQueryVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 车队与车辆关系版本表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface TenantVehicleRelVerMapper extends BaseMapper<TenantVehicleRelVer> {

    Integer updTenantVehicleRelVer(@Param("destVerState") Integer destVerState,
                                       @Param("vehicleCode")Long vehicleCode,
                                       @Param("tenantId")Long tenantId);

    TenantVehicleRelVer getTenantVehicleRelVer(@Param("relId")Long relId,
                                               @Param("verState")Integer verState);

    Map<String,Object> getTenantVehicleInfoVer(@Param("vehicleCode")Long vehicleCode,
                                               @Param("verState")Integer verState,
                                               @Param("tenantId")Long tenantId);
    Integer upDriverUserIdNull(@Param(("id"))Long id);


    List<TenantVehicleRelVer> getVehicle(@Param("userId") Long userId,
                                     @Param("tenantId") Long tenantId);

    List<TenantVehicleRelQueryDto> doQueryVehicleSimpleInfoNoPage(@Param("tenantVehicleRelQueryVo") TenantVehicleRelQueryVo tenantVehicleRelQueryVo);

    List<TenantVehicleRelQueryDto> doQueryBillReceiverNoPage(@Param("tenantVehicleRelQueryVo") TenantVehicleRelQueryVo tenantVehicleRelQueryVo);
}
