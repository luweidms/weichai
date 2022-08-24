package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.vehicle.VehicleObjectLineVer;
import org.apache.ibatis.annotations.Param;

/**
 * @Date:2021/12/28
 */
public interface VehicleObjectLineVerMapper extends BaseMapper<VehicleObjectLineVer> {

    Integer updVehicleObjectLineVer(@Param("verState")Integer verState,
                                       @Param("vvehicleCode")Long vvehicleCode);
}
