package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.vehicle.VehicleObjectLine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Date:2021/12/27
 */
public interface VehicleObjectLineMapper extends BaseMapper<VehicleObjectLine> {

    List<VehicleObjectLine> getVehicleObjectLine(@Param("vhicleCode")Long vhicleCode);
}
