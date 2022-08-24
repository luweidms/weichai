package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.vehicle.VehicleLineRel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Date:2021/12/27
 */
public interface VehicleLineRelMapper extends BaseMapper<VehicleLineRel> {

    List<VehicleLineRel> getVehiclelineRels(@Param("vehicleCode") Long vehicleCode,@Param("state")Integer state);
}
