package com.youming.youche.record.provider.mapper.vehicle;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youming.youche.record.domain.vehicle.VehicleIdle;
import com.youming.youche.record.vo.IdleVehiclesVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 闲置车辆表Mapper接口
 * </p>
 *
 * @author wuhao
 * @since 2022-05-18
 */
public interface VehicleIdleMapper extends BaseMapper<VehicleIdle> {


    /***
     * @Description: 查询闲置车辆
     * @Author: luwei
     * @Date: 2022/5/18 6:10 下午
     * @Param idleVehiclesVo:
     * @return: java.util.List<com.youming.youche.record.domain.vehicle.VehicleIdle>
     * @Version: 1.0
     **/
    List<IdleVehiclesVo> queryIdleVehicles(@Param("idleVehiclesVo") IdleVehiclesVo idleVehiclesVo,@Param("tenantId") Long tenantId);

}
