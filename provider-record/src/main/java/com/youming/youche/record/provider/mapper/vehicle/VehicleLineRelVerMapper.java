package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.vehicle.VehicleLineRelVer;
import com.youming.youche.record.domain.vehicle.VehicleObjectLineVer;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Date:2021/12/28
 */
public interface VehicleLineRelVerMapper extends BaseMapper<VehicleLineRelVer> {

    Integer updtVehicleLineRelVerState(@Param("destVerState")Integer destVerState,
                                       @Param("vehicleCode")Long vehicleCode);

    List<Map<String,Object>> getVehicleObjectLineVer(@Param("verState")Integer verState,
                                                     @Param("vehicleCode")Long vehicleCode);

    List<VehicleObjectLineVer> getVehicleObjectLineVerHis(@Param("verState")Integer verState,
                                                          @Param("vehicleCode")Long vehicleCode);

    List<VehicleLineRelVer> getVehiclelineRels(@Param("vehicleCode") Long vehicleCode,@Param("state")Integer state);
}
