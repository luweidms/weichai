package com.youming.youche.record.provider.mapper.vehicle;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.vehicle.VehicleDataInfo;
import com.youming.youche.record.domain.vehicle.VehicleDataInfoVer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 车辆版本表Mapper接口
 * </p>
 *
 * @author Terry
 * @since 2021-11-22
 */
public interface VehicleDataInfoVerMapper extends BaseMapper<VehicleDataInfoVer> {

    Integer updVehicleDataInfoVerState(@Param("verState") Integer verState,
                                       @Param("vvehicleCode") Long vvehicleCode);

    Integer updVehicleObjectLineVerState(@Param("verState") Integer verState,
                                         @Param("vvehicleCode") Long vvehicleCode);

    Integer updVehicleLineRelVerState(@Param("verState") Integer verState,
                                      @Param("vvehicleCode") Long vvehicleCode);

    Integer updTenantVehicleRelVerState(@Param("verState") Integer verState,
                                        @Param("vvehicleCode") Long vvehicleCode,
                                        @Param("tenantId") Long tenantId);


    Integer updTenantVehicleCostRelVerState(@Param("verState") Integer verState,
                                            @Param("vvehicleCode") Long vvehicleCode,
                                            @Param("tenantId") Long tenantId);

    Integer updTenantVehicleCertRelVerState(@Param("verState") Integer verState,
                                            @Param("vvehicleCode") Long vvehicleCode,
                                            @Param("tenantId") Long tenantId);

    VehicleDataInfoVer getVehicleDataInfoVer(@Param("vehicleCode")Long vehicleCode);


}

